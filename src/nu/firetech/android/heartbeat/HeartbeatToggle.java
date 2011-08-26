package nu.firetech.android.heartbeat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class HeartbeatToggle extends Activity {
	private static final String[] STATUS_CMD = new String[]{"su", "-c", "cat /sys/class/leds/cpu/trigger"};
	
	private static final String[] CONTROL_CMD = new String[]{"su", "-c", "cat > /sys/class/leds/cpu/trigger"};
	private static final String ON_MODE = "heartbeat";
	private static final String OFF_MODE = "none";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		toggle();
	}

	@Override
	public void onResume() {
		toggle();
	}

	private void toggle() {
		String status = exec(STATUS_CMD, null);
		
		boolean is_on = (status.indexOf("[" + ON_MODE + "]") != -1);
		
		exec(CONTROL_CMD, (is_on ? OFF_MODE : ON_MODE));
		Toast t = Toast.makeText(this, "CPU heartbeat LED " + (is_on ? "disabled" : "enabled"), Toast.LENGTH_LONG);
		t.show();
		
		// be nice
		this.finish();
	}

	private String exec(String[] commands, String input) {
		String s;
		StringBuilder sb = new StringBuilder();
		try {
			Process p = Runtime.getRuntime().exec(commands);
			InputStream stdout = p.getInputStream ();
			BufferedReader reader = new BufferedReader (new InputStreamReader(stdout));
			if (input != null) {
				OutputStream stdin = p.getOutputStream();
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin));
				writer.write(input);
				writer.close();
			}
			p.waitFor();
			while ((s = reader.readLine ()) != null) {
				sb.append(s);
				sb.append('\n');
			}
		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
		}
		
		if (sb.length() != 0) {
			sb.deleteCharAt(sb.length()-1);
		}

		return sb.toString();
	}
}