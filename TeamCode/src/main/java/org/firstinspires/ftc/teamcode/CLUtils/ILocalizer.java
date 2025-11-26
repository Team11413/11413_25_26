package org.firstinspires.ftc.teamcode.CLUtils;


import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

/**
 * Interface for localization methods.
 */
public interface ILocalizer {
    void setPose(Pose2D pose);

    /**
     * Returns the current pose estimate.
     * NOTE: Does not update the pose estimate;
     * you must call update() to update the pose estimate.
     * @return the Localizer's current pose
     */
    Pose2D getPose();

    /**
     * Updates the Localizer's pose estimate.
     */
    void update();
}
