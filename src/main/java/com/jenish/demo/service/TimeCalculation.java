package com.jenish.demo.service;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TimeCalculation {

	public Time diffOfTime(Time checkIn, Time checkOut) {
		String totTime = null;
		long result = 0;

		try {
			SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
			Date date1 = format.parse(checkIn.toString());
			Date date2 = format.parse(checkOut.toString());

			//System.out.println(date2.getTime() + " - " + date1.getTime());
			result = date2.getTime() - date1.getTime();

			//System.out.println("result = " + result);

			int seconds = (int) ((result / 1000) % 60);
			int minutes = (int) (result / (1000 * 60) % 60);
			int hours = (int) (result / (1000 * 60 * 60) % 24);
			totTime = timeFormat(hours) + ":" + timeFormat(minutes) + ":" + timeFormat(seconds);
			//System.out.println(hours + ":" + minutes + ":" + seconds);
			return Time.valueOf(totTime);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private String timeFormat(int val) {
		if (val < 10)
			return "0" + val;
		return "" + val;
	}

	public Time addTimes(Time checkIn, Time checkOut) {
		ArrayList<Time> times = new ArrayList<Time>();
		times.add(checkIn);
		times.add(checkOut);

		long tm = 0;
		for (Time t : times) {
			String[] arr = t.toString().split(":");
			tm += Integer.parseInt(arr[2]);
			tm += 60 * Integer.parseInt(arr[1]);
			tm += 3600 * Integer.parseInt(arr[0]);
		}

		long hh = tm / 3600;
		tm %= 3600;
		long mm = tm / 60;
		tm %= 60;
		long ss = tm;
		String result = format(hh) + ":" + format(mm) + ":" + format(ss);
		//System.out.println(result);
		return Time.valueOf(result);
	}

	private static String format(long s) {
		if (s < 10)
			return "0" + s;
		else
			return "" + s;
	}

}
