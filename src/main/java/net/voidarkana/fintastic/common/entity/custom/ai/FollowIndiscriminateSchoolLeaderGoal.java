package net.voidarkana.fintastic.common.entity.custom.ai;

import com.mojang.datafixers.DataFixUtils;
import net.minecraft.world.entity.ai.goal.Goal;
import net.voidarkana.fintastic.common.entity.custom.base.SchoolingFish;

import java.util.List;
import java.util.function.Predicate;

public class FollowIndiscriminateSchoolLeaderGoal extends Goal {
    private static final int INTERVAL_TICKS = 200;
    private final SchoolingFish mob;
    private int timeToRecalcPath;
    private int nextStartTick;

    public FollowIndiscriminateSchoolLeaderGoal(SchoolingFish pFish) {
        this.mob = pFish;
        this.nextStartTick = this.nextStartTick(pFish);
    }

    protected int nextStartTick(SchoolingFish pTaskOwner) {
        return reducedTickDelay(200 + pTaskOwner.getRandom().nextInt(200) % 20);
    }

    public boolean canUse() {
        if (this.mob.hasFollowers()) {
            return false;
        } else if (this.mob.isFollower()) {
            return true;
        } else if (this.nextStartTick > 0) {
            --this.nextStartTick;
            return false;
        } else {
            this.nextStartTick = this.nextStartTick(this.mob);
            Predicate<SchoolingFish> predicate = (fish) -> {
                return fish.canBeFollowed() || !fish.isFollower();
            };
            List<? extends SchoolingFish> list = this.mob.level().getEntitiesOfClass(this.mob.getClass(), this.mob.getBoundingBox().inflate(8.0D, 8.0D, 8.0D), predicate);
            SchoolingFish abstractschoolingfish = DataFixUtils.orElse(list.stream().filter(SchoolingFish::canBeFollowed).findAny(), this.mob);
            abstractschoolingfish.addFollowers(list.stream().filter((fish) -> {
                return !fish.isFollower();
            }));
            return this.mob.isFollower();
        }
    }

    public boolean canContinueToUse() {
        return this.mob.isFollower() && this.mob.inRangeOfLeader();
    }

    public void start() {
        this.timeToRecalcPath = 0;
    }

    public void stop() {
        this.mob.stopFollowing();
    }

    public void tick() {
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = this.adjustedTickDelay(10);
            this.mob.pathToLeader();
        }
    }
}