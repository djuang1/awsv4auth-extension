package org.mule.extension.awsv4auth.internal;

import static org.mule.runtime.extension.api.annotation.param.MediaType.ANY;
import static org.mule.runtime.extension.api.annotation.param.Optional.PAYLOAD;

import org.apache.commons.io.IOUtils;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.NullSafe;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.*;

import org.mule.runtime.extension.api.annotation.values.OfValues;

import org.mule.extension.awsv4auth.api.Headers;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class AWSV4AuthOperations {

	String strSignedHeader;

	@MediaType(value = ANY, strict = false)
	public String getAuthorizationString(@Optional(defaultValue = PAYLOAD) InputStream body, String accessKey,
			String secretKey, String regionName, String serviceName,
			@OfValues(StaticHTTPRequestTypeValueProvider.class) String requestType,
			@ParameterGroup(name = "Headers") Headers headers, String canonicalURI, @Optional String queryString) {

		String xAmzDate = headers.getTimeStamp();

		// System.out.println(IOUtils.toString(body));

		/* Task 1 - Create a Canonical Request */
		String canonicalRequest = null;
		try {
			canonicalRequest = prepareCanonicalRequest(IOUtils.toString(body), headers, regionName, serviceName,
					requestType, canonicalURI, queryString);
		} catch (IOException e) {
			e.printStackTrace();
		}

		//System.out.println(canonicalRequest);

		/* Task 2 - Create a String to Sign */
		String stringToSign = prepareStringToSign(canonicalRequest, xAmzDate, regionName, serviceName);

		/* Task 3 - Calculate the Signature */
		byte[] signatureKey = new byte[0];
		byte[] signature = new byte[0];
		try {
			signatureKey = getSignatureKey(secretKey, getDate(), regionName, serviceName);
			signature = HmacSHA256(signatureKey, stringToSign);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String strHexSignature = toHex(signature);

		/* Task 4 - Return the Authorization String */
		return buildAuthorizationString(strHexSignature, strSignedHeader, accessKey, regionName, serviceName);
	}

	private static byte[] hash(String text) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(text.getBytes("UTF-8"));
			return md.digest();
		} catch (Exception e) {
			throw new RuntimeException("Unable to compute hash while signing request: " + e.getMessage(), e);
		}
	}

	private static byte[] hash(byte[] data) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(data);
			return md.digest();
		} catch (Exception e) {
			throw new RuntimeException("Unable to compute hash while signing request: " + e.getMessage(), e);
		}
	}

	private static String toHex(byte[] data) {
		StringBuilder sb = new StringBuilder(data.length * 2);
		for (int i = 0; i < data.length; i++) {
			String hex = Integer.toHexString(data[i]);
			if (hex.length() == 1) {
				// Append leading zero.
				sb.append("0");
			} else if (hex.length() == 8) {
				// Remove ff prefix from negative numbers.
				hex = hex.substring(6);
			}
			sb.append(hex);
		}
		return sb.toString().toLowerCase(Locale.getDefault());
	}

	private String getTimeStamp() {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		return dateFormat.format(new Date());
	}

	private String getDate() {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		return dateFormat.format(new Date());
	}

	private String encodeParameter(String param) {
		try {
			return URLEncoder.encode(param, "UTF-8");
		} catch (Exception e) {
			return URLEncoder.encode(param);
		}
	}

	private String prepareStringToSign(String canonicalURL, String xAmzDate, String regionName, String serviceName) {
		String stringToSign = "";
		stringToSign = "AWS4-HMAC-SHA256" + "\n";
		stringToSign += xAmzDate + "\n";
		stringToSign += getDate() + "/" + regionName + "/" + serviceName + "/" + "aws4_request" + "\n";
		stringToSign += toHex(hash(canonicalURL));
		return stringToSign;
	}

	private byte[] HmacSHA256(byte[] key, String data) throws Exception {
		String algorithm = "HmacSHA256";
		Mac mac = Mac.getInstance(algorithm);
		mac.init(new SecretKeySpec(key, algorithm));
		return mac.doFinal(data.getBytes("UTF8"));
	}

	private byte[] getSignatureKey(String key, String date, String regionName, String serviceName) throws Exception {
		byte[] kSecret = ("AWS4" + key).getBytes("UTF8");
		byte[] kDate = HmacSHA256(kSecret, date);
		byte[] kRegion = HmacSHA256(kDate, regionName);
		byte[] kService = HmacSHA256(kRegion, serviceName);
		byte[] kSigning = HmacSHA256(kService, "aws4_request");
		return kSigning;
	}

	private String buildAuthorizationString(String strSignature, String strSignedHeader, String accessKey,
			String regionName, String serviceName) {
		return "AWS4-HMAC-SHA256" + " " + "Credential=" + accessKey + "/" + getDate() + "/" + regionName + "/"
				+ serviceName + "/" + "aws4_request" + ", " + "SignedHeaders=" + strSignedHeader + ", " + "Signature="
				+ strSignature;
	}

	private String prepareCanonicalRequest(String payload, Headers headers, String regionName, String serviceName,
			String requestType, String canonicalURI, String queryString) {

		TreeMap<String, String> queryParameters = new TreeMap<>();
		StringBuilder canonicalURL = new StringBuilder("");

		canonicalURL.append(requestType.toString()).append("\n");
		canonicalURI = canonicalURI == null || canonicalURI.trim().isEmpty() ? "/" : canonicalURI;
		canonicalURL.append(canonicalURI).append("\n");

		StringBuilder canonicalQueryString = new StringBuilder("");

		if (queryString != null && !queryString.isEmpty()) {

			String[] pairs = queryString.split("&");
			for (String pair : pairs) {
				int idx = pair.indexOf("=");
				try {
					queryParameters.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
							URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}

			for (Map.Entry<String, String> entrySet : queryParameters.entrySet()) {
				String key = entrySet.getKey();
				String value = entrySet.getValue();
				canonicalQueryString.append(key).append("=").append(encodeParameter(value)).append("&");
			}
			canonicalQueryString.deleteCharAt(canonicalQueryString.lastIndexOf("&"));
			canonicalQueryString.append("\n");
		} else {
			canonicalQueryString.append("\n");
		}
		
		canonicalURL.append(canonicalQueryString);		
		canonicalURL.append(getCanonicalizedHeaderString(headers));
		canonicalURL.append("\n");
		
		strSignedHeader = getSignedHeadersString(headers).substring(0, getSignedHeadersString(headers).length() - 1).toLowerCase();
		canonicalURL.append(strSignedHeader).append("\n");	
		
		String hashPayload = payload == null ? "" : payload;
		byte[] contentHash = hash(hashPayload);

		String contentHashString = toHex(contentHash);
		canonicalURL.append(contentHashString);

		return canonicalURL.toString();
	}
	
	protected String getCanonicalizedHeaderString(Headers headers) {
		
		final List<String> sortedHeaders = new ArrayList<String>(headers.getAdditionalHeaders().keySet());
		sortedHeaders.add("x-amz-date");
		sortedHeaders.add("host");
		sortedHeaders.add("content-type");
		Collections.sort(sortedHeaders, String.CASE_INSENSITIVE_ORDER);

		final Map<String, Object> requestHeaders = headers.getAdditionalHeaders();
		StringBuilder buffer = new StringBuilder();

		for (String header : sortedHeaders) {

			String key = header.toLowerCase();
			String value = (String) requestHeaders.get(header);

			buffer.append(key);
			buffer.append(":");
			if (value != null) {
				buffer.append(value);
			} else {
				switch (key) {
					case "content-type":
						buffer.append(headers.getContentType());
						break;
					case "x-amz-date":
						buffer.append(headers.getTimeStamp());
						break;
					case "host":
						buffer.append(headers.getHostName());
						break;
				}
			}			
			buffer.append("\n");
		}
		
		return buffer.toString();
	}
	
	protected String getSignedHeadersString(Headers headers) {
		
		final List<String> sortedHeaders = new ArrayList<String>(headers.getAdditionalHeaders().keySet());
		sortedHeaders.add("x-amz-date");
		sortedHeaders.add("host");
		sortedHeaders.add("content-type");
		Collections.sort(sortedHeaders, String.CASE_INSENSITIVE_ORDER);

		StringBuilder buffer = new StringBuilder();

		for (String header : sortedHeaders) {
			String key = header.toLowerCase();
			buffer.append(key);
			buffer.append(";");			
		}		
		
		return buffer.toString();
	}
}
