package ossbackup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
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
public class CommonRpc {
	private static List listMessage = new ArrayList();

	public static void main(String[] args) {
		// 获取配置文件信息
		String path = CommonRpc.class.getResource("/").getPath() + "ossbackup" + File.separator + "properties.txt";
		Map<String, String> keyMap = txt2String(path);
		// 获取备份实例名称
		if (args.length > 0) {
			String caseName = args[0];
			listMessage.add(getBackMessage(caseName, keyMap));
		} else {
			listMessage.add("Please enter the name of the instance that needs to be backed up！");
		}
		// 生成日志文件
		makeLogFile(keyMap.get("loginfoAddress"));
	}

	private static String getBackMessage(String caseName, Map<String, String> keyMap) {
		DefaultProfile profile = DefaultProfile.getProfile("cn-shenzhen", keyMap.get("accessKeyId"),
				keyMap.get("accessSecret"));
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
			System.out.println(response.getData());
			data = response.getData();
		} catch (ServerException e) {
			e.printStackTrace();
		} catch (ClientException e) {
			e.printStackTrace();
		}
		return data;

	}

	/**
	 * 
	 * 方法说明：读取配置文件信息
	 * 
	 * @autho chenyou
	 * @time 2018年11月29日 上午11:16:09
	 */
	public static Map txt2String(String filePath) {
		StringBuilder result = new StringBuilder();
		Map<String, String> map = new HashMap();
		File file = new File(filePath);
		if (file.exists())
			try {
				BufferedReader br = new BufferedReader(new FileReader(file));// 构造一个BufferedReader类来读取文件
				String s = null;
				while ((s = br.readLine()) != null) {// 使用readLine方法，一次读一行
					String[] str = s.split("=");
					if (str.length > 1)
						map.put(str[0], str[1]);
				}
				br.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		return map;
	}

	private static void makeLogFile(String filePath) {
		try {
			writeTxtFile(creatTxtFile(filePath));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 创建文件
	 * 
	 * @throws IOException
	 */
	public static String creatTxtFile(String filePath) throws IOException {
		filePath = filePath + File.separator + "logout.txt";
		File filename = new File(filePath);
		if (!filename.exists()) {
			filename.createNewFile();
		}
		return filePath;
	}

	/**
	 * 写文件
	 * 
	 * @param newStr
	 *            新内容
	 * @throws IOException
	 */
	public static boolean writeTxtFile(String filePath) throws IOException {
		// 先读取原有文件内容，然后进行写入操作
		boolean flag = false;
		String filein = "21231" + "\r\n";
		String temp = "";

		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;

		FileOutputStream fos = null;
		PrintWriter pw = null;
		try {
			// 文件路径
			File file = new File(filePath);
			// 将文件读入输入流
			fis = new FileInputStream(file);
			isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);
			StringBuffer buf = new StringBuffer();
			for (Object object : listMessage) {
				buf.append(object + "\r\n");
			}

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
