package com.xianyu.component.retryjob.enums;

import com.xianyu.component.utils.enums.EnumUtils;
import com.xianyu.component.utils.enums.EnumValue;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RetryJobStatusEnum implements EnumValue<Integer> {

	/**
	 * 处理中
	 */
	PROCESSING(0, "处理中"),
	/**
	 * 成功(终态)
	 */
	SUCCESS(1, "成功"),
	/**
	 * 失败(终态)
	 */
	FAILED(2, "失败");


	private final Integer value;

	private final String message;


	/**
	 * 根据值获取枚举
	 * @param value 值
	 * @return
	 * @author
	 * @date 2023/2/25
	 */
	public static Optional<RetryJobStatusEnum> parse(Integer value) {
		return EnumUtils.getByValue(RetryJobStatusEnum.class,value);
	}

	public boolean isFinish() {
		return this == SUCCESS || this == FAILED;
	}
}