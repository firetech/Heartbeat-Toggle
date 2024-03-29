/*
 * Copyright (C) 2011 Joakim Andersson
 * 
 * This file is part of HeartbeatToggle, an Android application to toggle
 * the "heartbeat" blinking of the CPU activity LED on rooted Notion Ink
 * Adam's (made for Adamcomb 0.3, but might work elsewhere).
 * 
 * HeartbeatToggle is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * HeartbeatToggle is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

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
	private static final String STATUS_CMD = "cat /sys/class/leds/cpu/trigger";
	
	private static final String CONTROL_CMD = "echo %s > /sys/class/leds/cpu/trigger";
	private static final String ON_MODE = "heartbeat";
	private static final String OFF_MODE = "none";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// be nice
		this.finish();
		
		boolean is_on = (suExec(STATUS_CMD).indexOf("[" + ON_MODE + "]") != -1);
		
		suExec(String.format(CONTROL_CMD, (is_on ? OFF_MODE : ON_MODE)));
		
		Toast t = Toast.makeText(this, "CPU heartbeat LED " + (is_on ? "disabled" : "enabled"), Toast.LENGTH_LONG);
		t.show();
	}

	private String suExec(String command) {
		String s;
		StringBuilder sb = new StringBuilder();
		try {
			Process p = Runtime.getRuntime().exec("su");
			InputStream stdout = p.getInputStream ();
			BufferedReader reader = new BufferedReader (new InputStreamReader(stdout));
			OutputStream stdin = p.getOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin));
			writer.write(command);
			writer.write('\n');
			writer.close();
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