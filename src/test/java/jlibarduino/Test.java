package jlibarduino;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;

import tm1638.Tm1638.Echo;

public class Test {
	public static void main(String[] args) throws Exception {
		Echo echo = Echo.newBuilder().setMessage("abc").setId(123).build();
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		echo.writeDelimitedTo(output);
		byte[] buffer = output.toByteArray();
		ByteArrayInputStream input = new ByteArrayInputStream(buffer);
		Class<?> messageClass = Echo.class;
		Method m = messageClass.getMethod("parseDelimitedFrom", InputStream.class);
		Object object = m.invoke(null, input);
		echo = (Echo) object;
		System.out.println(echo.getMessage());
		System.out.println(echo.getId());
	}
}
