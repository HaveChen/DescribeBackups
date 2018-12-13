package backUp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;

/*
pom.xml
<dependency>
  <groupId>com.aliyun</groupId>
  <artifactId>aliyun-java-sdk-core</artifactId>
  <version>4.0.3</version>
</dependency>
*/
public class DBBackUp {

	private static final String filePaths = "E:/ftpFile";

	public static void main(String[] args) {
		// ��ȡ�����ļ���Ϣ
		String path = filePaths + File.separator + "properties.txt";
		Map<String, String> keyMap = txt2String(path);
		// ��ȡ����ʵ������
		if (args.length == 0) {
			// rm-wz9e6mgmi403u848a
			String caseName = "rm-wz9e6mgmi403u848a";
			String data = getBackMessage(caseName, keyMap);
			// ����data���ݣ���ȡ���µı���url
			analysisURl(data);
		} else {
			System.out.println("Please enter the name of the instance that needs to be backed up��");
		}
	}

	private static String analysisURl(String data) {
		String url = "";
		JSONObject dataJson = new JSONObject(data);
		if (data.isEmpty()) {
			System.out.println("get data  result is null!");
		} else {
			if (dataJson.isNull("Items")) {
				// ֱ�ӷŻش�����Ϣ
				System.out.println("data:" + data);
			} else {
				JSONObject Items = new JSONObject(dataJson.get("Items").toString());
				if (Items.isNull("Backup")) {
					// ֱ�ӷŻش�����Ϣ
					System.out.println("items" + data);
				} else {
					JSONArray backUp = new JSONArray(Items.get("Backup").toString());
					if (backUp != null) {
						JSONObject its = new JSONObject(backUp.get(0).toString());
						url = its.getString("BackupDownloadURL");
						System.out.println(url);
						// д��txt�ļ�
						try {
							writeTxtFile(filePaths + File.separator + "backUpUrl.txt", url);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

				}
			}

		}
		return url;

	}

	private static String getBackMessage(String caseName, Map<String, String> keyMap) {
		DefaultProfile profile = DefaultProfile.getProfile("cn-shenzhen", keyMap.get("accessKeyId"),
				keyMap.get("accessSecretId"));
		// DefaultProfile profile = DefaultProfile.getProfile("cn-shenzhen",
		// "LTAImN9vCL40twAQ",
		// "j9Rku9glKn23hzmtMZQEnJaI35DfCU");
		IAcsClient client = new DefaultAcsClient(profile);
		String data = "";
		CommonRequest request = new CommonRequest();
		request.setMethod(MethodType.POST);
		request.setDomain("rds.aliyuncs.com");
		request.setVersion(keyMap.get("version"));
		request.setAction("DescribeBackups");
		request.putQueryParameter("DBInstanceId", caseName);
		try {
			CommonResponse response = client.getCommonResponse(request);
			// System.out.println(response.getData());
			data = response.getData();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;

	}

	/**
	 * 
	 * ����˵������ȡ�����ļ���Ϣ
	 * 
	 * @autho chenyou
	 * @time 2018��11��29�� ����11:16:09
	 */
	public static Map txt2String(String filePath) {
		StringBuilder result = new StringBuilder();
		Map<String, String> map = new HashMap();
		File file = new File(filePath);
		if (file.exists()) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(file));// ����һ��BufferedReader������ȡ�ļ�
				String s = null;
				while ((s = br.readLine()) != null) {// ʹ��readLine������һ�ζ�һ��
					String[] str = s.split("=");
					if (str.length > 1)
						map.put(str[0], str[1]);
				}
				br.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("this file ii not exists :" + filePath);
		}
		return map;
	}

	/**
	 * д�ļ�
	 * 
	 * @param newStr
	 *            ������
	 * @throws IOException
	 */
	public static boolean writeTxtFile(String filePath, String mes) throws IOException {

		File file = new File(filePath);
		if (!file.exists()) {
			file.createNewFile();
		}
		// �ȶ�ȡԭ���ļ����ݣ�Ȼ�����д�����
		boolean flag = false;
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;

		FileOutputStream fos = null;
		PrintWriter pw = null;
		try {
			// ���ļ�����������
			fis = new FileInputStream(file);
			isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);
			StringBuffer buf = new StringBuffer();
			buf.append(mes + "\r\n");

			fos = new FileOutputStream(file);
			pw = new PrintWriter(fos);
			pw.write(buf.toString().toCharArray());
			pw.flush();
			flag = true;
		} catch (IOException e1) {
			throw e1;
		} finally {
			if (pw != null) {
				pw.close();
			}
			if (fos != null) {
				fos.close();
			}
			if (br != null) {
				br.close();
			}
			if (isr != null) {
				isr.close();
			}
			if (fis != null) {
				fis.close();
			}
		}
		return flag;
	}
}
