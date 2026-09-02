package org.crafterscr.craftersnpcanimations.animation.bend;

import org.crafterscr.craftersnpcanimations.animation.emote.NpcEmote;
import org.crafterscr.craftersnpcanimations.animation.emote.NpcEmoteBoneTrack;
import org.crafterscr.craftersnpcanimations.animation.emote.NpcEmoteInterpolator;

public final class NpcBendController {

    private NpcBendController() {
    }

    public static NpcBendPose sample(
            NpcEmote emote,
            float animationTick
    ) {

        NpcBendPose pose =
                new NpcBendPose();

        if (emote == null) {
            return pose;
        }

        pose.setRightArm(
                sampleBone(
                        emote,
                        "rightArm",
                        animationTick
                )
        );

        pose.setLeftArm(
                sampleBone(
                        emote,
                        "leftArm",
                        animationTick
                )
        );

        pose.setRightLeg(
                sampleBone(
                        emote,
                        "rightLeg",
                        animationTick
                )
        );

        pose.setLeftLeg(
                sampleBone(
                        emote,
                        "leftLeg",
                        animationTick
                )
        );

        return pose;
    }

    private static float sampleBone(
            NpcEmote emote,
            String boneName,
            float tick
    ) {

        NpcEmoteBoneTrack track =
                emote.bone(
                        boneName
                );

        if (track == null) {
            return 0.0F;
        }

        if (!track.has(
                NpcEmoteBoneTrack.Channel.BEND
        )) {
            return 0.0F;
        }

        return NpcEmoteInterpolator
                .sampleChannel(
                        track,
                        NpcEmoteBoneTrack.Channel.BEND,
                        tick
                );
    }
}