package com.liangmayong.booth;

/**
 * BoothOutputStream
 * 
 * @author LiangMaYong
 * @version 1.0
 */
abstract class BoothOutputStream extends StreamRuleData {

	public BoothOutputStream() {
		super(new BoothDataRule());
	}

}
