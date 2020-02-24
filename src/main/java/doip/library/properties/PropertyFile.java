package doip.library.properties;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import doip.library.util.Conversion;

/**
 * This class is a convenient class to read data from a property file.
 * 
 * @author Marco Wehnert
 *
 */
public class PropertyFile {

	/**
	 * Represents all the properties from a property file
	 */
	private Properties properties = null;

	/**
	 * Constructor with the name of the property file.
	 * 
	 * @param filename Name of the property file
	 * @throws IOException Will be thrown when the property file could not be
	 *                     loaded.
	 */
	public PropertyFile(String filename) throws IOException {
		this.loadProperties(filename);
	}

	/**
	 * Loads all the properties from a property file.
	 * 
	 * @param filename Name of the property file.
	 * @throws IOException Will be thrown when the file could not be read.
	 */
	public void loadProperties(String filename) throws IOException {
		FileInputStream fileInputStream = new FileInputStream(filename);
		this.properties = new Properties();
		this.properties.load(fileInputStream);
	}

	/**
	 * Returns the value of the property given by the key
	 * 
	 * @param key The name of the property.
	 * @return Returns the value of the property given by key. If the key does
	 *         not exist it returns null. If the key is defined but the value is
	 *         empty it returns an empty string ( = "").
	 */
	public String getProperty(String key) {
		return this.properties.getProperty(key);
	}

	public String getPropertyAsString(String key, boolean mandatory)
			throws MissingProperty, EmptyPropertyValue {

		if (mandatory) {
			return this.getMandatoryPropertyAsString(key);
		} else {
			return this.getOptionalPropertyAsString(key);
		}
	}
	
	public boolean getPropertyAsBoolean(String key, boolean mandatory) 
			throws MissingProperty, EmptyPropertyValue {
		if (mandatory) {
			return this.getMandatoryPropertyAsBoolean(key);
		} else {
			return this.getOptionalPropertyAsBoolean(key);
		}
	}

	public InetAddress getPropertyAsInetAddress(String key, boolean mandatory)
			throws UnknownHostException, MissingProperty, EmptyPropertyValue {
		if (mandatory) {
			return this.getMandatoryPropertyAsInetAddress(key);
		} else {
			return this.getOptionalPropertyAsInetAddress(key);
		}
	}

	public int getPropertyAsInt(String key, boolean mandatory)
			throws MissingProperty, EmptyPropertyValue {
		if (mandatory) {
			return this.getMandatoryPropertyAsInt(key);
		} else {
			return this.getOptionalPropertyAsInt(key);
		}
	}

	public byte[] getPropertyAsByteArray(String key, boolean mandatory)
			throws MissingProperty, EmptyPropertyValue {
		if (mandatory) {
			return this.getMandatoryPropertyAsByteArray(key);
		} else {
			return this.getOptionalPropertyAsByteArray(key);
		}
	}

	public String getMandatoryPropertyAsString(String key)
			throws MissingProperty, EmptyPropertyValue {
		String value = this.properties.getProperty(key);
		if (value == null)
			throw new MissingProperty(key);
		if (value == "")
			throw new EmptyPropertyValue(key);
		return value;
	}

	public String getOptionalPropertyAsString(String key)
			throws EmptyPropertyValue {
		String value = this.properties.getProperty(key);
		if (value == null)
			return null;
		if (value == "")
			throw new EmptyPropertyValue(key);
		return value;
	}
	
	public boolean getOptionalPropertyAsBoolean(String key) 
			throws EmptyPropertyValue {
		String value = this.properties.getProperty(key);
		if (value == null) return false;
		if (value == "") throw new EmptyPropertyValue(key);
		if (value.toLowerCase().equals("true")) return true;
		return false;
	}

	public InetAddress getMandatoryPropertyAsInetAddress(String key)
			throws MissingProperty, EmptyPropertyValue, UnknownHostException {
		String value = this.properties.getProperty(key);
		if (value == null)
			throw new MissingProperty(key);
		if (value == "")
			throw new EmptyPropertyValue(key);
		InetAddress address = InetAddress.getByName(value);
		return address;
	}

	public InetAddress getOptionalPropertyAsInetAddress(String key)
			throws EmptyPropertyValue, UnknownHostException {
		String value = this.properties.getProperty(key);
		if (value == null)
			return null;
		if (value == "")
			throw new EmptyPropertyValue(key);
		InetAddress address = InetAddress.getByName(value);
		return address;
	}

	public int getMandatoryPropertyAsInt(String key)
			throws MissingProperty, EmptyPropertyValue {
		String value = this.properties.getProperty(key);
		if (value == null)
			throw new MissingProperty(key);
		if (value == "")
			throw new EmptyPropertyValue(key);
		if (value.startsWith("0x")) {
			return Integer.parseInt(value.substring(2), 16);
		}  else {
			return Integer.parseInt(value);
		}
	}
	
	public boolean getMandatoryPropertyAsBoolean(String key) 
			throws MissingProperty, EmptyPropertyValue {
		String value = this.properties.getProperty(key);
		if (value == null) throw new MissingProperty(key);
		if (value == "") throw new EmptyPropertyValue(key);
		if (value.toLowerCase().equals("true")) return true;
		return false;
	}

	public int getOptionalPropertyAsInt(String key) throws EmptyPropertyValue {
		String value = this.properties.getProperty(key);
		if (value == null)
			return 0;
		if (value == "")
			throw new EmptyPropertyValue(key);
		if (value.startsWith("0x")) {
			return Integer.parseInt(value.substring(2), 16);
		}  else {
			return Integer.parseInt(value);
		}
	}

	public byte[] getMandatoryPropertyAsByteArray(String key)
			throws MissingProperty, EmptyPropertyValue {
		String value = this.properties.getProperty(key);
		if (value == null)
			throw new MissingProperty(key);
		if (value == "")
			throw new EmptyPropertyValue(key);
		value = value.replace("0x", "");
		value = value.replace(" ", "");
		return Conversion.hexStringToByteArray(value);
	}

	public byte[] getOptionalPropertyAsByteArray(String key)
			throws EmptyPropertyValue {
		String value = this.properties.getProperty(key);
		if (value == null)
			return null;
		if (value == "")
			throw new EmptyPropertyValue(key);
		value = value.replace("0x", "");
		value = value.replace(" ", "");
		return Conversion.hexStringToByteArray(value);
	}
}
