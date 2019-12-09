package com.github.unchama.seichiassist.task;

import com.github.unchama.seichiassist.SeichiAssist;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

public class AsyncEntityRemover extends BukkitRunnable {
    private Entity e;

    public AsyncEntityRemover(Entity e) {
        this.e = e;
    }

    @Override
    public void run() {
        SeichiAssist.managedEntities().$minus$eq(e);
        e.remove();
    }

}
