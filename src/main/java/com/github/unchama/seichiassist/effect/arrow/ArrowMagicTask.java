package com.github.unchama.seichiassist.effect.arrow;

import com.github.unchama.seichiassist.SeichiAssist;
import com.github.unchama.seichiassist.data.PlayerData;
import com.github.unchama.seichiassist.util.ItemMetaFactory;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

public class ArrowMagicTask extends SkeletonEffectTask<ThrownPotion> {
	SeichiAssist plugin = SeichiAssist.instance;
	HashMap<UUID,PlayerData> playermap = SeichiAssist.playermap;
	Player player;
	Location ploc;
	UUID uuid;
	PlayerData playerdata;
	long tick;
	ItemStack i;
	PotionMeta pm;

	public ArrowMagicTask(Player player) {
		this.tick = 0;
		this.player = player;
		//プレイヤーの位置を取得
		this.ploc = player.getLocation();
		//UUIDを取得
		this.uuid = player.getUniqueId();
		//ぷれいやーでーたを取得
		this.playerdata = playermap.get(uuid);
		//ポーションデータを生成
		this.i = new ItemStack(Material.SPLASH_POTION);
		this.pm = ItemMetaFactory.SPLASH_POTION.getValue();
		pm.setBasePotionData(new PotionData(PotionType.INSTANT_HEAL));
		i.setItemMeta(pm);

		//発射する音を再生する.
		player.playSound(ploc, Sound.ENTITY_WITCH_THROW, 1, 1.3f);

		//スキルを実行する処理
		Location loc = player.getLocation().clone();
		loc.add(loc.getDirection()).add(0,1.6,0);
		Vector vec = loc.getDirection();
		double k = 0.8;
		vec.setX(vec.getX() * k);
		vec.setY(vec.getY() * k);
		vec.setZ(vec.getZ() * k);
		proj = player.getWorld().spawn(loc, ThrownPotion.class);
		SeichiAssist.entitylist.add(proj);
		proj.setShooter(player);
		proj.setGravity(false);
		proj.setItem(i);
		//読み込み方法
		/*
		 * Projectile proj = event.getEntity();
			if ( proj instanceof Arrow && proj.hasMetadata("ArrowSkill") ) {
			}
		 */
		proj.setMetadata("ArrowSkill", new FixedMetadataValue(plugin, true));
		proj.setVelocity(vec);
	}
	@Override
	public void run() {
		tick ++;
		if(tick > 100){
			proj.remove();
			SeichiAssist.entitylist.remove(proj);
			cancel();
		}
	}

}
