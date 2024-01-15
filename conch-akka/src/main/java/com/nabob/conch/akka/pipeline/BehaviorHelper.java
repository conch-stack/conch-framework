package com.nabob.conch.akka.pipeline;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;
import com.nabob.conch.akka.pipeline.dto.PhotoImage;
import com.nabob.conch.akka.pipeline.dto.PhotoLabel;
import com.nabob.conch.akka.pipeline.dto.PhotoMsg;

import java.util.Random;

/**
 * BehaviorHelper
 *
 * @author Adam
 * @since 2024/1/14
 */
public class BehaviorHelper {

    public Behavior<PhotoLabel> getPhotoLabelJob1() {
        return Behaviors.receive((context, photoLabel) -> {
            // 完成车牌识别的功能
            String license = getLicense(photoLabel.getPhotoImage());

            PhotoMsg photoMsg = new PhotoMsg();
            photoMsg.id = photoLabel.id;
            photoMsg.license = license;

            // 通知下游
            photoLabel.to.tell(photoMsg);
            return Behaviors.same();
        });
    }

    private String getLicense(PhotoImage photoImage) {
        return "1314" + photoImage.getBytes();
    }

    public Behavior<PhotoLabel> getPhotoLabelJob2() {
        return Behaviors.receiveMessage((photoLabel) -> {
            // 完成车辆测速的功能
            int speed = getSpeed(photoLabel.getPhotoImage());

            PhotoMsg photoMsg = new PhotoMsg();
            photoMsg.id = photoLabel.id;
            photoMsg.speed = speed;

            // 通知下游
            photoLabel.to.tell(photoMsg);

            return Behaviors.same();
        });
    }

    private int getSpeed(PhotoImage photoImage) {
        return (int) (Math.random() * 100);
    }
}
