/*-
 * ============LICENSE_START=======================================================
 * Ran Simulator Controller
 * ================================================================================
 * Copyright (C) 2020 Wipro Limited.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.ransim.rest.client;

import java.io.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;

import javax.net.ssl.*;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.eclipse.persistence.internal.oxm.conversion.Base64;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class RestClient {

    private static class NullHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }
    private static class SavingTrustManager implements X509TrustManager {

        private final X509TrustManager tm;
        private X509Certificate[] chain;

        SavingTrustManager(X509TrustManager tm) {
            this.tm = tm;
        }

        public X509Certificate[] getAcceptedIssuers() {

            return new X509Certificate[0];

        }

        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            throw new UnsupportedOperationException();
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            this.chain = chain;
            tm.checkServerTrusted(chain, authType);
        }
    }

    static Logger log = Logger.getLogger(RestClient.class.getName());

    HttpHeaders createHeaders(String username, String password) {
        return new HttpHeaders() {
            {
                String auth = username + ":" + password;
                byte[] encodedAuth = Base64.base64Encode(auth.getBytes(Charset.forName("US-ASCII")));

                String authHeader = "Basic " + new String(encodedAuth);
                set("Authorization", authHeader);
                set("Content-Type", "application/xml");
                set("Accept", "application/xml");
            }
        };
    }

    /**
     * Sends mount request to sdnr.
     *
     * @param serverId
     *        netconf server id name
     * @param ip
     *        server ip address
     * @param port
     *        port number
     * @param agentIp
     *        agent ip address
     * @param agentPort
     *        agent port number
     * @param agentUsername
     *        agent username
     * @param agentPassword
     *        agent password
     * @return returns the message to be passed
     */

    public String sendMountRequestToSdnr(String serverId, String ip, int port, String agentIp, String agentPort,
            String agentUsername, String agentPassword) {

        ResponseEntity<String> result = null;
        try {
            String requestBody = "<node xmlns=\"urn:TBD:params:xml:ns:yang:network-topology\">    <node-id> " + serverId
                    + " </node-id>    <username xmlns=\"urn:opendaylight:netconf-node-topology\">admin</username>    <password xmlns=\"urn:opendaylight:netconf-node-topology\">admin</password>    <host xmlns=\"urn:opendaylight:netconf-node-topology\">"
                    + agentIp + "</host>    <schema-cache-directory xmlns=\"urn:opendaylight:netconf-node-topology\">"
                    + serverId + "</schema-cache-directory>    <port xmlns=\"urn:opendaylight:netconf-node-topology\">"
                    + agentPort
                    + "</port>    <tcp-only xmlns=\"urn:opendaylight:netconf-node-topology\">false</tcp-only>    <schemaless xmlns=\"urn:opendaylight:netconf-node-topology\">false</schemaless>    <max-connection-attempts xmlns=\"urn:opendaylight:netconf-node-topology\">0</max-connection-attempts>    <connection-timeout-millis xmlns=\"urn:opendaylight:netconf-node-topology\">20000</connection-timeout-millis>    <default-request-timeout-millis xmlns=\"urn:opendaylight:netconf-node-topology\">60000</default-request-timeout-millis>    <sleep-factor xmlns=\"urn:opendaylight:netconf-node-topology\">1.1</sleep-factor>    <between-attempts-timeout-millis xmlns=\"urn:opendaylight:netconf-node-topology\">2000</between-attempts-timeout-millis>    <reconnect-on-changed-schema xmlns=\"urn:opendaylight:netconf-node-topology\">false</reconnect-on-changed-schema>    <keepalive-delay xmlns=\"urn:opendaylight:netconf-node-topology\">60</keepalive-delay>    <concurrent-rpc-limit xmlns=\"urn:opendaylight:netconf-node-topology\">0</concurrent-rpc-limit>    <actor-response-wait-time xmlns=\"urn:opendaylight:netconf-node-topology\">60</actor-response-wait-time></node>";

            String response = "";
            HttpsURLConnection connection = null;
            BufferedReader br = null;
            log.info("Change in  http to https");
            char[] passphrase;
            String p = "changeit";
            passphrase = p.toCharArray();
            File file = new File("jssecacerts");
            if (file.isFile() == false) {
                char SEP = File.separatorChar;
                File dir = new File(SEP + "tmp" + SEP + "ransim-install" + SEP + "config");
                file = new File(dir, "jssecacerts");
            }
            log.info("Loading new  KeyStores" + file + "...");
            InputStream in = new FileInputStream(file);
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(in, passphrase);
            in.close();
            SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(new SSLContextBuilder()
                    .loadTrustMaterial(null, new TrustSelfSignedStrategy()).loadKeyMaterial(ks, passphrase).build(),
                    NoopHostnameVerifier.INSTANCE);

            HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();

            ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);

            SSLContext context = SSLContext.getInstance("TLS");
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(ks);
            X509TrustManager defaultTrustManager = (X509TrustManager) tmf.getTrustManagers()[0];
            SavingTrustManager tm = new SavingTrustManager(defaultTrustManager);
            context.init(null, new TrustManager[] {tm}, null);

            SSLContext.setDefault(context);
            SSLSocketFactory factory = context.getSocketFactory();

            log.info("Using Authorization");

            SSLSocket socket = (SSLSocket) factory.createSocket(ip, port);
            socket.setSoTimeout(10000);

            try {
                socket.startHandshake();
            } catch (SSLException e) {

                log.error("Exc insocket handshake", e);

            }

            log.info("Started SSL handshake without hostname verifier...");

            RestTemplate restTemplate = new RestTemplate(requestFactory);
            HttpClientBuilder httpClientBuilder = HttpClients.custom().setSSLContext(SSLContext.getDefault())
                    .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).useSystemProperties();

            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClientBuilder.build()));

            HttpHeaders headers = createHeaders(agentUsername, agentPassword);

            log.info("request : " + requestBody);
            log.info("headers : " + headers);
            for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                log.info("Key:" + entry.getKey() + "  , Value:" + entry.getValue());
            }
            String url = "https://" + ip + ":" + port
                    + "/restconf/config/network-topology:network-topology/topology/topology-netconf/node/" + serverId;

            HttpEntity<String> entity = new HttpEntity<String>(requestBody, headers);
            result = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);

            log.info("Request sent, result: " + result);
            socket.close();
        } catch (SSLException e) {
            System.out.println();
            e.printStackTrace(System.out);
        }

        catch (Exception e) {

            log.error("Exc in post {}", e);
        }
        return result.toString();
    }

    /**
     * Sends an unmount request to sdnr.
     *
     * @param serverId
     *        netconf server id name
     * @param ip
     *        ip address
     * @param port
     *        port number
     * @param sdnrUsername
     *        sdnr username
     * @param sdnrPassword
     *        sdnr password
     * @return returns the message to be passed
     */
    public String sendUnmountRequestToSdnr(String serverId, String ip, int port, String sdnrUsername,
            String sdnrPassword) {
        String url = "http://" + ip + ":" + port
                + "/restconf/config/network-topology:network-topology/topology/topology-netconf/node/" + serverId;
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = createHeaders(sdnrUsername, sdnrPassword);
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
        log.info("request sent, result: " + result);
        return result.toString();
    }
}
