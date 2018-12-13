package backUp;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 请求外部接口工具�? 公司: 海云天测�?
 * 
 * @autho chenyou
 * @time 2018�?11�?27�? 下午2:57:34
 */
public class UrlUtil {

	/**
	 * 接口请求
	 * 
	 * @param path
	 *            请求地址
	 */
	public static void interfaceUtil(String path) {
		try {
			URL url = new URL(path);
			// 打开和url之间的连�?
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			PrintWriter out = null;
			// 请求方式
			conn.setRequestMethod("POST");
			// //设置通用的请求属�?
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			// 设置是否向httpUrlConnection输出，设置是否从httpUrlConnection读入，此外发送post请求必须设置这两�?
			// �?常用的Http请求无非是get和post，get请求可以获取静�?�页面，也可以把参数放在URL字串后面，传递给servlet�?
			// post与get�?
			// 不同之处在于post的参数不是放在URL字串里面，�?�是放在http请求的正文内�?
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发�?�请求参数即数据
			// out.print(data);
			// 缓冲数据
			out.flush();
			// 获取URLConnection对象对应的输入流
			InputStream is = conn.getInputStream();
			// 构�?�一个字符流缓存
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String str = "";
			while ((str = br.readLine()) != null) {
				System.out.println(str);
			}
			// 关闭�?
			is.close();
			// 断开连接，最好写上，disconnect是在底层tcp
			// socket链接空闲时才切断。如果正在被其他线程使用就不切断�?
			// 固定多线程的话，如果不disconnect，链接会增多，直到收发不出信息�?�写上disconnect后正常一些�??
			conn.disconnect();
			System.out.println("完整结束");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			downLoadFromUrlHttp(
					"https://rdsbak-st-v2.oss-cn-shenzhen.aliyuncs.com/custins3173817/hins3834331_data_20181128233803.tar.gz?OSSAccessKeyId=LTAIyKzxtSYNknVO&Expires=1543651175&Signature=C%2FIypafTV4EN%2FEh1mziLI1Li4Vs%3D",
					"test.zip", "E:/ftpFile2");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// ͨ��url�����ļ�
	public static void downLoadFromUrlHttp(String urlStr, String fileName, String savePath) throws IOException {
		URL url = new URL(urlStr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		// ���ó�ʱ��Ϊ3��
		conn.setConnectTimeout(3 * 1000);
		// ��ֹ���γ���ץȡ������403����
		conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
		conn.connect();

		// �õ�������
		InputStream inputStream = conn.getInputStream();
		byte[] getData = readInputStream(inputStream);
		// �ļ�����λ��
		File saveDir = new File(savePath);
		if (!saveDir.exists()) {
			saveDir.mkdirs();
		}
		// �����
		File file = new File(saveDir + File.separator + fileName);
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(getData);
		if (fos != null) {
			fos.close();
		}
		if (inputStream != null) {
			inputStream.close();
		}
	}

	public static byte[] readInputStream(InputStream inputStream) throws IOException {
		byte[] b = new byte[1024];
		int len = 0;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		while ((len = inputStream.read(b)) != -1) {
			bos.write(b, 0, len);
		}
		bos.close();
		return bos.toByteArray();
	}

}
