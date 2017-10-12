import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;


public class Main {
    public static void main(String[] args) throws java.io.IOException {
        final Message message = new Message();

        System.out.println("started recording sounds...");
        while(true){
            try {
                AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100,16,2,4,44100,false);
//                AudioFormat audioFormat = new AudioFormat( 1200,8,2,true,false);
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

                        File file;

                        long time = System.currentTimeMillis();
                        file = new File("sounds/"+time+".wav");

                        try {
                            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE,file);
                            System.out.println("stopped recording");
                            message.publishMessage(file.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };

                thread.start();
                Thread.sleep(10000);
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