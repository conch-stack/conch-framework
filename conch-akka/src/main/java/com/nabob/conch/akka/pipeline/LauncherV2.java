package com.nabob.conch.akka.pipeline;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Props;
import akka.actor.typed.javadsl.Behaviors;
import com.nabob.conch.akka.pipeline.dto.Command;
import com.nabob.conch.akka.pipeline.dto.Photo;
import com.nabob.conch.akka.pipeline.dto.PhotoImage;
import com.nabob.conch.akka.pipeline.dto.PhotoMsg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @author Adam
 * @since 2024/1/15
 */
public class LauncherV2 {

    /**
     * ---------------------------------start--------------------------------------------
     * >>> Press ENTER to run <<<
     * 1
     * getPhotoLabelJob2 sleep 2s
     * Photo(speed=71, license=1314testBytes0)
     * 1已过期删除
     * ---------------------------------end--------------------------------------------
     *
     *
     * ---------------------------------start--------------------------------------------
     * >>> Press ENTER to run <<<
     * 2
     * getPhotoLabelJob2 sleep 2s
     * getPhotoLabelJob2 sleep 2s
     * Photo(speed=26, license=1314testBytes0)
     * 2已过期删除
     * 3已过期删除
     * ---------------------------------end--------------------------------------------
     *
     *
     * ---------------------------------start--------------------------------------------
     * >>> Press ENTER to run <<<
     * 3已过期删除
     */
    public static void main(String[] args) {

        LauncherV2 launcher = new LauncherV2();

        PipelineContext pipelineContext = new PipelineContext();
        pipelineContext.setBehaviorHelper(new BehaviorHelper());

        // 超时
        pipelineContext.setTimeout(Duration.ofSeconds(2));

        ActorSystem<PhotoImage> photoSystem = ActorSystem.create(SplitterV2.create(pipelineContext), "photoSystem");

        ActorRef<Command> photoAggSystemV2 = photoSystem.systemActorOf(AggregatorV2.create(pipelineContext), "photoAggSystemV2", Props.empty());
        pipelineContext.setAggregatorV2(photoAggSystemV2);

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
