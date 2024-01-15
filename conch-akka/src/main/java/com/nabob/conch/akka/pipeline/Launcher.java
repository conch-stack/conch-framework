package com.nabob.conch.akka.pipeline;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Props;
import akka.actor.typed.javadsl.Behaviors;
import com.nabob.conch.akka.pipeline.dto.Photo;
import com.nabob.conch.akka.pipeline.dto.PhotoImage;
import com.nabob.conch.akka.pipeline.dto.PhotoMsg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @author Adam
 * @since 2024/1/15
 */
public class Launcher {

    public static void main(String[] args) {

        Launcher launcher = new Launcher();

        PipelineContext pipelineContext = new PipelineContext();
        pipelineContext.setBehaviorHelper(new BehaviorHelper());

        ActorSystem<PhotoImage> photoSystem = ActorSystem.create(Splitter.create(pipelineContext), "photoSystem");

        ActorRef<PhotoMsg> photoAggSystem = photoSystem.systemActorOf(Aggregator.create(pipelineContext), "photoAggSystem", Props.empty());
        pipelineContext.setAggregator(photoAggSystem);

        ActorRef<Photo> photoResultSystem = photoSystem.systemActorOf(Behaviors.setup(ResultStream::new), "photoResultSystem", Props.empty());
        pipelineContext.setResultStream(photoResultSystem);

        try {
            while (true) {
                System.out.println("---------------------------------start--------------------------------------------");
                System.out.println(">>> Press ENTER to run <<<");
                // Enter data using BufferReader
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                // Reading data using readLine
                String timesStr = reader.readLine();
                if (timesStr.equals("end")) {
                    break;
                }

                int times = Integer.parseInt(timesStr);
                for (int i = 0; i < times; i++) {
                    launcher.test(photoSystem, i);
                }

                Thread.sleep(TimeUnit.SECONDS.toMillis(3));
                System.out.println("---------------------------------end--------------------------------------------");
                System.out.println();
                System.out.println();
            }
        } catch (IOException ignored) {
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            // 或stop掉当前System下所有的Actor
            photoSystem.terminate();
        }

    }

    private void test(ActorSystem<PhotoImage> photoSystem, int i) {
        PhotoImage photoImage = new PhotoImage();
        photoImage.setBytes("testBytes" + i);
        photoSystem.tell(photoImage);
    }

}
