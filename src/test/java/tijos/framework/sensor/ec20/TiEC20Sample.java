package tijos.framework.sensor.ec20;


import tijos.framework.devicecenter.TiUART;
import tijos.framework.sensor.ec20.mqtt.IMQTTEventListener;
import tijos.framework.sensor.ec20.mqtt.MQTTClient;
import tijos.framework.sensor.ec20.mqtt.MQTTConnectOptions;
import tijos.framework.util.Delay;

class MQTTEventListner implements IMQTTEventListener {

	MQTTClient sample ;
	public MQTTEventListner(MQTTClient sample) {
		this.sample = sample;
	}
	
	@Override
	public void onPublishDataArrived( int msgId, String topic, String message) {
		System.out.println("Data arrived " + msgId + " topic " + topic + " message " + message);
	}

	@Override
	public void onLinkLost(int error) {
		System.out.println("Data onMQTTLinkLost " + " error " + error);
	}
	
}


/**
 * Hello world!
 */
public class TiEC20Sample {
	public static void main(String[] args) {
		System.out.println("Hello World!");

		try {
			TiUART uart = TiUART.open(4);

			uart.setWorkParameters(8, 1, TiUART.PARITY_NONE, 115200);

			TiEC20 ec20 = new TiEC20(uart);
			
			Delay.msDelay(1000);
			System.out.println("Start...");
			ec20.startup();

			System.out.println(" IMSI : " + ec20.getIMSI());
			System.out.println(" IMEI : " + ec20.getIMEI());
			System.out.println(" RSSI : " + ec20.getRSSI());
			System.out.println(" ICCID : " + ec20.getICCID());

			System.out.println(" Is attached :" + ec20.isNetworkAttached());
			System.out.println(" Is registered : " + ec20.isNetworkRegistred());

			System.out.println("IP Address " + ec20.getIPAddress());
 	
			MQTTClient mqtt = ec20.getMqttClient("mqtt.tijcloud.com", 1883, MQTTClient.generateClientId());
			
			mqtt.setEventListener(new MQTTEventListner(mqtt));
			
			MQTTConnectOptions options = new MQTTConnectOptions();
			mqtt.connect(options);

			mqtt.subscribe(1, "topic1", "topic2");
	
			for(int i = 0; i < 10; i ++) {
				mqtt.publish("topic1", "this is a test2", 1, false);
				Delay.msDelay(1000);
			}
			
			
			int loop = 100;
			while(loop -- > 0) {
				Delay.msDelay(1000);
			}
			
			mqtt.disconnect();
			
			
			Delay.msDelay(10000);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
