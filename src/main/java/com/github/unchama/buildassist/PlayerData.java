/*** Eclipse Class Decompiler plugin, copyright (c) 2012 Chao Chen (cnfree2000@hotmail.com) ***/
package com.github.unchama.buildassist;

import com.github.unchama.seichiassist.SeichiAssist;
import com.github.unchama.seichiassist.data.player.BuildCount;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.UUID;

public class PlayerData {
	public String name;
	public UUID uuid;
	public int level;
	//トータル設置ブロック数
	public BigDecimal totalbuildnum;

	public boolean flyflag;
	public int flytime;
	public boolean Endlessfly ;
	public boolean ZoneSetSkillFlag ;
	public boolean zsSkillDirtFlag;
	public int AREAint ;

	public BigDecimal build_num_1min;			//1分のブロック設置数

	//ブロックを並べるスキル設定フラグ
	public int line_up_flg;
	public int line_up_step_flg;
	public int line_up_des_flg;
	public int line_up_minestack_flg;
	//ブロック範囲設置スキル設定フラグ
	public boolean zs_minestack_flag;

	//プレイヤーデータクラスのコンストラクタ
	public PlayerData(Player player){
		//初期値を設定
		name = Util.getName(player);
		uuid = player.getUniqueId();
		totalbuildnum = BigDecimal.ZERO;
		level = 1;
		flyflag = false;
		flytime = 0;
		Endlessfly = false;
		ZoneSetSkillFlag = false;
		zsSkillDirtFlag = true;
		AREAint = 2;

		line_up_flg = 0;
		line_up_step_flg = 0;
		line_up_des_flg = 0;
		line_up_minestack_flg = 0;

		zs_minestack_flag = false;

		build_num_1min = BigDecimal.ZERO;

	}
	//レベルを更新
	public void updateLevel(Player player) {
		calcPlayerLevel(player);
	}

	//プレイヤーレベルを計算し、更新する。
	private void calcPlayerLevel(Player player){
		//現在のランクの次を取得
		int i = level;
		//ランクが上がらなくなるまで処理
		while(BuildAssist.levellist.get(i).intValue() <= totalbuildnum.doubleValue() && (i+2) <= BuildAssist.levellist.size()){
			if(!BuildAssist.DEBUG){
				//レベルアップ時のメッセージ
				player.sendMessage(ChatColor.GOLD+"ﾑﾑｯﾚﾍﾞﾙｱｯﾌﾟ∩( ・ω・)∩【建築Lv(" + i +")→建築Lv(" + (i+1) + ")】");
			}
			i++;
			if( (i+1) == BuildAssist.levellist.size()){
				player.sendMessage(ChatColor.GOLD+"最大Lvに到達したよ(`･ω･´)");
			}
		}
		level = i;
	}

	//オフラインかどうか
	public boolean isOffline() {
		return BuildAssist.plugin.getServer().getPlayer(uuid) == null;
	}

	//建築系データを読み込む　ture:読み込み成功　false:読み込み失敗
	public boolean buildload(Player player){
		com.github.unchama.seichiassist.data.player.PlayerData playerdata_s = SeichiAssist.Companion.getPlayermap().get(uuid);
		if(playerdata_s == null){
			return false;
		}
		int server_num = SeichiAssist.Companion.getSeichiAssistConfig().getServerNum();

		final BuildCount oldBuildCount = playerdata_s.getBuildCount();

		totalbuildnum = playerdata_s.getBuildCount().getCount();
		//ブロック設置カウントが統合されてない場合は統合する
		if(server_num >= 1 && server_num <= 3){
			byte f = playerdata_s.getBuildCount().getMigrationFlag();
			if( (f & (0x01 << server_num))  == 0 ){
				if(f == 0) {
					// 初回は加算じゃなくベースとして代入にする
					totalbuildnum = BuildBlock.calcBuildBlock(player);
				} else {
					totalbuildnum = totalbuildnum.add(BuildBlock.calcBuildBlock(player));
				}
				f = (byte) (f | (0x01 << server_num));
				final BuildCount updatedBuildCount = playerdata_s.getBuildCount().copy(oldBuildCount.getLv(), totalbuildnum, f);
				playerdata_s.setBuildCount(updatedBuildCount);

				player.sendMessage(ChatColor.GREEN+"サーバー" + server_num + "の建築データを統合しました");
				if(f == 0x0E){
					player.sendMessage(ChatColor.GREEN+"全サーバーの建築データを統合しました");
				}
			}
		}
		level = playerdata_s.getBuildCount().getLv();
		updateLevel(player);
		return true;
	}

	//建築系データを保存
	public void buildsave(Player player){
		com.github.unchama.seichiassist.data.player.PlayerData playerData = SeichiAssist.Companion.getPlayermap().get(uuid);
		if (playerData == null){
			player.sendMessage(ChatColor.RED+"建築系データ保存失敗しました");
			return;
		}

		final BuildCount oldBuildCount = playerData.getBuildCount();

		//1分制限の判断
		final BigDecimal newBuildCount;
		if (build_num_1min.doubleValue() <= BuildAssist.config.getBuildNum1minLimit()) {
			newBuildCount = totalbuildnum.add(build_num_1min);
		} else {
			newBuildCount = totalbuildnum.add(new BigDecimal(BuildAssist.config.getBuildNum1minLimit()));
		}

		playerData.setBuildCount(new BuildCount(level, newBuildCount, oldBuildCount.getMigrationFlag()));
	}

}
