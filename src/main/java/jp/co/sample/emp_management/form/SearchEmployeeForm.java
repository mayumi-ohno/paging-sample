package jp.co.sample.emp_management.form;

import javax.validation.constraints.NotBlank;

/**
 * 従業員曖昧検索時に使用するフォーム.
 * 
 * @author igamasayuki
 * 
 */
public class SearchEmployeeForm {
	/** 従業員名 */
	@NotBlank(message = "従業員名を入力してください")
	private String name;

	// 以下、Getter,Setter
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
