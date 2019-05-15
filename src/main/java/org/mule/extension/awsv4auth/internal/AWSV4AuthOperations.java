package org.mule.extension.awsv4auth.internal;

import static org.mule.runtime.extension.api.annotation.param.MediaType.ANY;

import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.Optional;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class AWSV4AuthOperations {

  String strSignedHeader;

  @MediaType(value = ANY, strict = false)
  public String getAuthorizationString(String body, String timeStamp, String accessKey, String secretKey, String regionName, String serviceName, String hostName, String canonicalURL, @Optional String queryString) {

    String xAmzDate = timeStamp;

    //System.out.println(currentDate + ":" + xAmzDate);

    /* Task 1 - Create a Canonical Request */
    String canonicalRequest = prepareCanonicalRequest(body, xAmzDate, regionName, serviceName, hostName, canonicalURL, queryString);
    System.out.println(canonicalRequest);

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

  private String encodeParameter(String param){
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

  private String buildAuthorizationString(String strSignature, String strSignedHeader, String accessKey, String regionName, String serviceName) {
    return "AWS4-HMAC-SHA256" + " " + "Credential=" + accessKey + "/" + getDate() + "/" + regionName + "/" + serviceName + "/" + "aws4_request" + ", " + "SignedHeaders=" + strSignedHeader + ", " + "Signature=" + strSignature;
  }

  private String prepareCanonicalRequest(String payload, String timeStamp, String regionName, String serviceName, String hostName, String canonicalURI, String queryString) {

    TreeMap<String, String> queryParameters = new TreeMap<>();
    StringBuilder canonicalURL = new StringBuilder("");

    canonicalURL.append("POST").append("\n");
    canonicalURI = canonicalURI == null || canonicalURI.trim().isEmpty() ? "/" : canonicalURI;
    canonicalURL.append(canonicalURI).append("\n");

    StringBuilder canonicalQueryString = new StringBuilder("");

    if (queryString != null && !queryString.isEmpty()) {

      String[] pairs = queryString.split("&");
      for (String pair : pairs) {
        int idx = pair.indexOf("=");
        try {
          queryParameters.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
        }
      }

      for (Map.Entry<String, String> entrySet : queryParameters.entrySet()) {
        String key = entrySet.getKey();
        String value = entrySet.getValue();

        //System.out.println(key + ":" + value);

        canonicalQueryString.append(key).append("=").append(encodeParameter(value)).append("&");
      }
      canonicalQueryString.deleteCharAt(canonicalQueryString.lastIndexOf("&"));
      canonicalQueryString.append("\n");
    } else {
      canonicalQueryString.append("\n");
    }
    canonicalURL.append(canonicalQueryString);

    TreeMap<String, String> awsHeaders = new TreeMap<>();
    awsHeaders.put("x-amz-date", timeStamp);
    awsHeaders.put("host", hostName);
    awsHeaders.put("content-type", "application/json");
    //awsHeaders.put("content-length", payload.length() + "");

    StringBuilder signedHeaders = new StringBuilder("");
    if (awsHeaders != null && !awsHeaders.isEmpty()) {
      for (Map.Entry<String, String> entrySet : awsHeaders.entrySet()) {
        String key = entrySet.getKey().toLowerCase();
        String value = entrySet.getValue();
        signedHeaders.append(key).append(";");
        canonicalURL.append(key).append(":").append(value).append("\n");
      }
      canonicalURL.append("\n");
    } else {
      canonicalURL.append("\n");
    }

    strSignedHeader = signedHeaders.substring(0, signedHeaders.length() - 1).toLowerCase();
    canonicalURL.append(strSignedHeader).append("\n");

    String hashPayload = payload == null ? "" : payload;
    byte[] contentHash = hash(hashPayload);

    String contentHashString = toHex(contentHash);
    canonicalURL.append(contentHashString);

    return canonicalURL.toString();
  }
}
