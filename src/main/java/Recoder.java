import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class Recoder {
    public static void recode(){
        System.out.println("started recording sounds...");
        while(true){
            try {
                AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100,16,2,4,44100,false);

                DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
                if(!AudioSystem.isLineSupported(info)){
                    System.err.print("Line is not supported");
                }
                final TargetDataLine targetDataLine = (TargetDataLine)AudioSystem.getLine(info);
                targetDataLine.open();

                System.out.println("started recording...");
                targetDataLine.start();

                Thread thread = new Thread(){
                    @Override public void run(){
                        AudioInputStream audioInputStream = new AudioInputStream(targetDataLine);
                        File file = new File("record.wav");
                        try {
                            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE,file);
                            System.out.println("stopped recording");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };

                thread.start();
                Thread.sleep(5000);
                targetDataLine.stop();
                targetDataLine.close();

                System.out.println("End...");

            } catch (LineUnavailableException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
