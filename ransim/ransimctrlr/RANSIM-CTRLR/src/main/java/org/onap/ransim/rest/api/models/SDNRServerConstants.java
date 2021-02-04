package org.onap.ransim.rest.api.models;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.beans.factory.annotation.Value;

@Configuration
@PropertySource(value = "file:/tmp/ransim-install/config/sdnrServer.properties")
public class SDNRServerConstants {

	@Value("${sdnrServerIp:}")
	private static String sdnrServerIp;

	@Value("${sdnrServerPort:0}")
	private static int sdnrServerPort;

	@Value("${sdnrServerUserid:}")
	private static String sdnrServerUserid;

	@Value("${sdnrServerPassword:}")
	private static String sdnrServerPassword;

	public static String getSdnrServerIp() {
		return sdnrServerIp;
	}

	public static void setSdnrServerIp(String sdnrServerIp) {
		SDNRServerConstants.sdnrServerIp = sdnrServerIp;
	}

	public static int getSdnrServerPort() {
		return sdnrServerPort;
	}

	public static void setSdnrServerPort(int sdnrServerPort) {
		SDNRServerConstants.sdnrServerPort = sdnrServerPort;
	}

	public static String getSdnrServerUserid() {
		return sdnrServerUserid;
	}

	public static void setSdnrServerUserid(String sdnrServerUserid) {
		SDNRServerConstants.sdnrServerUserid = sdnrServerUserid;
	}

	public static String getSdnrServerPassword() {
		return sdnrServerPassword;
	}

	public static void setSdnrServerPassword(String sdnrServerPassword) {
		SDNRServerConstants.sdnrServerPassword = sdnrServerPassword;
	}

}
