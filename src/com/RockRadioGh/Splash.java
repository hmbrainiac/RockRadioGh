package com.RockRadioGh;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Splash extends Activity {

    /**
     * Called when the activity is first created.
     */
    ImageView play,stop,record,download_img,advert,increase_v,decrease_v;
    TextView song,download_txt;
    private static final int RECORDER_SAMPLERATE = 8000;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private AudioRecord recorder = null;
    private Thread recordingThread = null;
    private boolean isRecording = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //setButtonHandlers();
        enableButton();

        int bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,
                RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
    }
    private void enableButton() {
        play = (ImageView)findViewById(R.id.play_img_btn);
        stop = (ImageView)findViewById(R.id.stop_img_btn);
        record=(ImageView)findViewById(R.id.record_img_btn);
        download_img=(ImageView)findViewById(R.id.download_img);
        increase_v=(ImageView)findViewById(R.id.increase_volume);
        decrease_v = (ImageView)findViewById(R.id.decrease_volume);
        advert = (ImageView)findViewById(R.id.add_space);
        song = (TextView)findViewById(R.id.song_Playing);
        download_txt = (TextView)findViewById(R.id.download_txt);


        increase_v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO increase volume
            }
        });


        if(isRecording == true)
            record.setImageResource(R.drawable.recording);
        else
            record.setImageResource(R.drawable.to_record);
        decrease_v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO decrease volume
            }
        });

        record.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(isRecording == true){
                    stopRecording();
                }
                if (isRecording == false){
                    startRecording();
                }

            }
        });
    }



    int BufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we use only 1024
    int BytesPerElement = 2;

    private void startRecording() {

        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE, RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING, BufferElements2Rec * BytesPerElement);

        recorder.startRecording();
        record.setImageResource(R.drawable.recording);
        isRecording = true;
        recordingThread = new Thread(new Runnable() {
            public void run() {
                writeAudioDataToFile();
            }
        }, "AudioRecorder Thread");
        recordingThread.start();
    }

    //convert short to byte
    private byte[] short2byte(short[] sData) {
        int shortArrsize = sData.length;
        byte[] bytes = new byte[shortArrsize * 2];
        for (int i = 0; i < shortArrsize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        return bytes;

    }

    private void writeAudioDataToFile() {
        // Write the output audio in byte

        String filePath = "/sdcard/voice8K16bitmono.pcm";
        short sData[] = new short[BufferElements2Rec];

        FileOutputStream os = null;
        try {
            os = new FileOutputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (isRecording) {
            // gets the voice output from microphone to byte format

            recorder.read(sData, 0, BufferElements2Rec);
            System.out.println("Short wirting to file" + sData.toString());
            try {
                // // writes the data to file from buffer
                // // stores the voice buffer
                byte bData[] = short2byte(sData);
                os.write(bData, 0, BufferElements2Rec * BytesPerElement);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        // stops the recording activity
        if (null != recorder) {
            record.setImageResource(R.drawable.to_record);
            isRecording = false;
            recorder.stop();
            recorder.release();
            recorder = null;
            recordingThread = null;
        }
    }




}
