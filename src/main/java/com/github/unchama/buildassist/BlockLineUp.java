package com.github.unchama.buildassist;

import com.github.unchama.buildassist.util.ExternalPlugins;
import com.github.unchama.seichiassist.MineStackObjectList;
import com.github.unchama.seichiassist.SeichiAssist;
import com.github.unchama.seichiassist.minestack.MineStackObj;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.UUID;
//import org.bukkit.metadata.FixedMetadataValue;
//import org.bukkit.plugin.java.JavaPlugin;
//import com.github.unchama.seichiassist.util.Util;

public class BlockLineUp implements Listener{

//    private JavaPlugin plugin;

//	public void BlockLineUp(JavaPlugin plugin) {
//		this.plugin = plugin;
//		plugin.getServer().getPluginManager().registerEvents(this, plugin);
//	}


	@EventHandler
	public void onPlayerClick(PlayerInteractEvent e){
		//プレイヤーを取得
		Player player = e.getPlayer();
		//UUID取得
		UUID uuid = player.getUniqueId();
		//ワールドデータを取得
		World playerworld = player.getWorld();
		//プレイヤーが起こしたアクションを取得
		Action action = e.getAction();
		//アクションを起こした手を取得
		EquipmentSlot equipmentslot = e.getHand();
		//プレイヤーデータ
		com.github.unchama.seichiassist.data.player.PlayerData playerdata_s = SeichiAssist.Companion.getPlayermap().get(uuid);
		PlayerData playerdata = BuildAssist.playermap.get(uuid);

		//プレイヤーデータが無い場合は処理終了
		if(playerdata == null){
			return;
		}

		//スキルOFFなら終了
		if(playerdata.line_up_flg == 0){
			return;
		}

		//スキル利用可能でないワールドの場合終了
		if(Util.isSkillEnable(player) == false ){
			return;
		}
		//左クリックの処理
		if(action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK)){
			//プレイヤーインベントリを取得
			PlayerInventory inventory = player.getInventory();
			//メインハンドとオフハンドを取得
			ItemStack mainhanditem = inventory.getItemInMainHand();
			ItemStack offhanditem = inventory.getItemInOffHand();

//			player.sendMessage(mainhanditem.getType().toString());
//			player.sendMessage(mainhanditem.getData().toString());
//			player.sendMessage(""+mainhanditem.getAmount());	//持ってる数

			//メインハンドにブロックがあるとき
			if( BuildAssist.materiallist2.contains(mainhanditem.getType()) == true || BuildAssist.material_slab2.contains(mainhanditem.getType()) == true ) {
				if(offhanditem.getType() != Material.STICK){//オフハンドに木の棒を持ってるときのみ発動する
					return;
				}

				Location pl = player.getLocation();
				Material m = mainhanditem.getType();
				byte d = mainhanditem.getData().getData();

				//仰角は下向きがプラスで上向きがマイナス
				//方角は南を0度として時計回りに360度、何故か偶にマイナスの値になる
				float pitch = pl.getPitch();
				float yaw = (pl.getYaw() + 360) % 360;
//				player.sendMessage("方角：" + Float.toString(yaw) + "　仰角：" + Float.toString(pitch));
//				player.sendMessage("マナ:" + playerdata_s.activeskilldata.mana.getMana() );
				int step_x = 0;
				int step_y = 0;
				int step_z = 0;
				//プレイヤーの足の座標を取得
				int px = pl.getBlockX();
//				int py = pl.getBlockY()+1;
				int py = (int)(pl.getY() + 1.6);
				int pz = pl.getBlockZ();
				int no = -1;		//マインスタックのNo.
				int double_mag = 1;//ハーフブロック重ね起きしたときフラグ
				//プレイヤーの向いてる方向を判定
				if (pitch > 45 ){//下
					step_y = -1;
//					py--;
					py = pl.getBlockY();
				}else if (pitch < -45 ){//上
					step_y = 1;
				}else{
					if(playerdata.line_up_flg == 2){//下設置設定の場合は一段下げる
						py--;
					}
					if (yaw > 315 || yaw < 45 ){//南
						step_z = 1;
					}else if(yaw < 135 ){//西
						step_x = -1;
					}else if(yaw < 225 ){//北
						step_z = -1;
					}else{//東
						step_x = 1;
					}
				}
				double mana_mag = BuildAssist.config.getblocklineupmana_mag();

				int max = mainhanditem.getAmount();//メインハンドのアイテム数を最大値に
				//マインスタック優先の場合最大値をマインスタックの数を足す
				if( playerdata.line_up_minestack_flg == 1 ){
					for(int cnt = 0; cnt < MineStackObjectList.INSTANCE.getMinestacklist().size() ; cnt++){
						final MineStackObj mineStackObj = MineStackObjectList.INSTANCE.getMinestacklist().get(cnt);
						if( m.equals( MineStackObjectList.INSTANCE.getMinestacklist().get(cnt).getMaterial() ) &&
								d == MineStackObjectList.INSTANCE.getMinestacklist().get(cnt).getDurability()){
							max += playerdata_s.getMinestack().getStackedAmountOf(mineStackObj);
							no = cnt;
//							player.sendMessage("マインスタックNo.：" + no + "　max：" + max);
							break;
						}

					}
					/*
					//石ハーフ
					if (m == Material.STEP && d == 0){
						max += playerdata_s.minestack.getNum(Util.MineStackobjname_indexOf("step0"));
					}
					*/
				}
				//マナが途中で足りなくなる場合はマナの最大にする
				if ( playerdata_s.getActiveskilldata().mana.getMana()- (double)(max) * mana_mag < 0.0 ){
					max = (int) (playerdata_s.getActiveskilldata().mana.getMana()/ mana_mag);
				}

				//手に持ってるのがハーフブロックの場合
				if(BuildAssist.material_slab2.contains(m) == true ){
					if(playerdata.line_up_step_flg == 0){
						d += 8;	//上設置設定の場合は上側のデータに書き換え
					}else if(playerdata.line_up_step_flg == 2){
						//両方設置の場合マテリアルの種類を変える
						if (m == Material.STONE_SLAB2){
							m = Material.DOUBLE_STONE_SLAB2;//赤砂岩
						}else if (m == Material.PURPUR_SLAB){
							m = Material.PURPUR_DOUBLE_SLAB;//プルパー
						}else if (m == Material.WOOD_STEP){
							m = Material.WOOD_DOUBLE_STEP;//木
						}else if (m == Material.STEP){
							m = Material.DOUBLE_STEP;//石
						}
						max /= 2;
						double_mag = 2;
					}

				}
//				player.sendMessage("max:" + max );
				//ループ数を64に制限
				if( max > 64 ){
					max = 64;
				}
				int v = 0;	//設置した数
				for( v = 0 ; v < max ; v++){//設置ループ
					px += step_x;
					py += step_y;
					pz += step_z;
					Block b = pl.getWorld().getBlockAt(px , py , pz );

					//空気以外にぶつかったら設置終わり
					if (b.getType() != Material.AIR){
//						player.sendMessage(":"+b.getType().toString());
						if(BuildAssist.material_destruction.contains(b.getType()) == false || playerdata.line_up_des_flg == 0){
//							player.sendMessage("stop:"+b.getType().toString());
							break;
						}
						Collection<ItemStack> i = b.getDrops();

						if(i.iterator().hasNext() == true){
							b.getLocation().getWorld().dropItemNaturally(pl, i.iterator().next());
						}
					}

					//他人の保護がかかっている場合は設置終わり
					if(!ExternalPlugins.getWorldGuard().canBuild(player, b.getLocation())){
						break;
					}

					pl.getWorld().getBlockAt(px , py , pz ).setType(m);
					pl.getWorld().getBlockAt(px , py , pz ).setData(d);		//ブロックのデータを設定

				}
				v *= double_mag;	//ハーフ2段重ねの場合は2倍
				//カウント対象ワールドの場合カウント値を足す
				if( Util.isBlockCount(player) == true){	//対象ワールドかチェック
					Util.addBuild1MinAmount(player, new BigDecimal(v * BuildAssist.config.getBlockCountMag()));	//設置した数を足す
				}

				//マインスタック優先の場合マインスタックの数を減らす
				if( playerdata.line_up_minestack_flg == 1 && no > -1){
					final MineStackObj mineStackObj = MineStackObjectList.INSTANCE.getMinestacklist().get(no);


					//設置した数vを再計算(下のメインハンドの処理に使用する為)
					/*
					 * TODO 変数vの意味が以下の様に変わっているので可読性が宜しくない
					 * (設置した数 -> 設置した数のうち、MineStack上で足りなかったブロック数)
					 */
					long num = playerdata_s.getMinestack().getStackedAmountOf(mineStackObj) - v;
					if( num < 0 ){ // minestack上の残数では足りない場合
						//minestackは0にする
						playerdata_s.getMinestack().subtractStackedAmountOf
							(mineStackObj , playerdata_s.getMinestack().getStackedAmountOf(mineStackObj));

						//minestack不足分をvへ代入
						v = (int)num * (-1);

					}else{ // minestack上の残数で足りる場合
						//minestack上から設置した数分引く
						playerdata_s.getMinestack().subtractStackedAmountOf(mineStackObj , v);

						//足りなかったブロックは0なのでvには0を代入
						v = 0;
					}
				}

				//アイテム数が0ならメインハンドのアイテムをクリア
				if (mainhanditem.getAmount() - v <= 0 ){
//					mainhanditem.setType(Material.AIR);
//					mainhanditem.setAmount(-1);
					inventory.setItemInMainHand(new ItemStack(Material.AIR,-1));//アイテム数が0になっても消えないので自前で消す
				}else{	//0じゃないなら設置した分を引く
					mainhanditem.setAmount(mainhanditem.getAmount() - v );

				}
//				playerdata_s.activeskilldata.mana.decreaseMana((double)(v) * mana_mag , player, playerdata_s.level);
				player.playSound(player.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);

//				player.sendMessage("v:" + v +" d:" + d);
//				player.sendMessage("マナ:" + playerdata_s.activeskilldata.mana.getMana() );

			}
		}
	}
}
