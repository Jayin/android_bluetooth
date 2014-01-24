package com.smartdevices.smartqwatchdemo;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder.AudioSource;
import android.util.Log;

public class DemoUtil {
	private static final String TAG="DemoUtil";
	public static final String KEY_RECORD_DATA = "record_data";
	public static final String KEY_CONNECT = "start_connect";
	
	private static int[] mSampleRates = new int[] { 8000, 11025, 22050, 44100 };
	public static int gBufferSize = -1;
	
	public static  AudioRecord findAudioRecord() {
		for (int rate : mSampleRates) {
			for (short audioFormat : new short[] {
					AudioFormat.ENCODING_PCM_8BIT,
					AudioFormat.ENCODING_PCM_16BIT }) {
				for (short channelConfig : new short[] {
						AudioFormat.CHANNEL_IN_STEREO,
						AudioFormat.CHANNEL_IN_MONO }) {
					try {
						int bufferSize = AudioRecord.getMinBufferSize(rate,
								channelConfig, audioFormat);

						Log.d(TAG, "findAudioRecord:Attempting rate " + rate + "Hz, bits: "
								+ audioFormat + ", channel: " + channelConfig
								+ ", bufferSizeInBytes:" + bufferSize);

						if (bufferSize > 0) {
							AudioRecord recorder = new AudioRecord(
									AudioSource.MIC, rate, channelConfig,
									audioFormat, bufferSize);

							if (recorder.getState() == AudioRecord.STATE_INITIALIZED) {
								gBufferSize = bufferSize;
								return recorder;
							}
							recorder.release();
							recorder = null;
						}
					} catch (Exception e) {
						Log.e(TAG, rate + "Exception, keep trying.", e);
					}
				}
			}
		}
		return null;
	}
	
	public static AudioTrack findAudioTrack() {
		for (int rate : mSampleRates) {
			for (short audioFormat : new short[] {
					AudioFormat.ENCODING_PCM_8BIT,
					AudioFormat.ENCODING_PCM_16BIT }) {
				for (short channelConfig : new short[] {
						AudioFormat.CHANNEL_IN_STEREO,
						AudioFormat.CHANNEL_IN_MONO }) {
					try {
						int bufferSize = AudioTrack.getMinBufferSize(rate,
								channelConfig, audioFormat);

						Log.d(TAG, "findAudioTrack:Attempting rate " + rate + "Hz, bits: "
								+ audioFormat + ", channel: " + channelConfig
								+ ", bufferSizeInBytes:" + bufferSize);

						if (bufferSize > 0) {
							AudioTrack track = new AudioTrack(
									AudioManager.STREAM_MUSIC, rate, channelConfig,
									audioFormat, bufferSize, AudioTrack.MODE_STREAM);

							if (track.getState() == AudioTrack.STATE_INITIALIZED) {
								gBufferSize = bufferSize;
								return track;
							}
							track.release();
							track = null;
						}
					} catch (Exception e) {
						Log.e(TAG, rate + "Exception, keep trying.", e);
					}
				}
			}
		}
		return null;
	}
}
