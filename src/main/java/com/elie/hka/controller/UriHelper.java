/*
 * Copyright (C) 2022 - present Juergen Zimmermann, Hochschule Karlsruhe
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.elie.hka.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import static com.elie.hka.controller.Constants.API_PATH;

/// Hilfsklasse um URIs für den Location-Header zu ermitteln, falls ein _Gateway API_ verwendet
/// wird.
///
/// @author [Jürgen Zimmermann](mailto:Juergen.Zimmermann@h-ka.de)
@Component
class UriHelper {
    private static final String X_FORWARDED_PROTO = "X-Forwarded-Proto";
    private static final String X_FORWARDED_HOST = "X-Forwarded-Host";
    private static final String X_FORWARDED_PORT = "X-Forwarded-Port";
    private static final String X_FORWARDED_PREFIX = "X-Forwarded-Prefix";
    private static final String KUNDEN_PREFIX = "/kunden";

    private final LazyConstant<Logger> logger = LazyConstant.of(() -> LoggerFactory.getLogger(UriHelper.class));

    /// Konstruktor mit _package private_ für _Spring_.
    UriHelper() {
    }

    /// Basis-URI ermitteln, d.h. ohne Query-Parameter.
    ///
    /// @param request Servlet-Request
    /// @return Die Basis-URI als String
    URI getBaseUri(final HttpServletRequest request) {
        final var forwardedHost = request.getHeader(X_FORWARDED_HOST);
        if (forwardedHost != null) {
            // Forwarding durch Gateway oder Spring Cloud Gateway
            logger.get().trace("getBaseUri: {}: {}", X_FORWARDED_HOST, forwardedHost);
            return getBaseUriForwarded(request, forwardedHost);
        }

        // KEIN Forwarding von einem Gateway
        // URI aus Schema, Host, Port und Pfad
        final var uriComponents = ServletUriComponentsBuilder.fromRequestUri(request).build();
        final var baseUri = uriComponents.getScheme() + "://" + uriComponents.getHost() + ':' +
            uriComponents.getPort() + '/' + API_PATH;
        logger.get().debug("getBaseUri (ohne Forwarding): baseUri={}", baseUri);
        return URI.create(baseUri);
    }

    @SuppressWarnings("UnnecessaryParentheses")
    private URI getBaseUriForwarded(final HttpServletRequest request, final String forwardedHost) {
        // X-Forwarded-Proto: "https" oder "http"
        // X-Forwarded-Port:   z.B. "443"
        // X-Forwarded-Server: Hostname des Gateways bzw. des Pod
        final var forwardedProto = request.getHeader(X_FORWARDED_PROTO);
        final var forwardedPort = request.getHeader(X_FORWARDED_PORT);
        if (forwardedProto == null || forwardedPort == null) {
            throw new IllegalStateException(
                X_FORWARDED_PROTO + ": " + forwardedProto + ", " + X_FORWARDED_PORT + ": " + forwardedPort
            );
        }
        logger.get().trace("getBaseUriForwarded: {}: {}", X_FORWARDED_PROTO, forwardedProto);
        logger.get().trace("getBaseUriForwarded: {}: {}", X_FORWARDED_PORT, forwardedPort);

        var forwardedPrefix = request.getHeader(X_FORWARDED_PREFIX);
        // X-Forwarded-Prefix: null bei Gateway bzw. "/kunden" bei Spring Cloud Gateway
        if (forwardedPrefix == null) {
            forwardedPrefix = KUNDEN_PREFIX;
        } else {
            logger.get().trace("getBaseUriForwarded: forwardedPrefix={}", forwardedPrefix);
        }

        final var baseUri = ("https".equals(forwardedProto) && "443".equals(forwardedPort)) ||
            ("http".equals(forwardedProto) && "80".equals(forwardedPrefix))
            ? forwardedProto + "://" + forwardedHost + forwardedPrefix + '/' + API_PATH
            : forwardedProto + "://" + forwardedHost + ':' + forwardedPort + forwardedPrefix + '/' + API_PATH;
        logger.get().trace("getBaseUriForwarded: baseUri={}", baseUri);
        return URI.create(baseUri);
    }
}
