package jp.co.sample.emp_management.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import jp.co.sample.emp_management.domain.Employee;
import jp.co.sample.emp_management.form.InsertEmployeeForm;
import jp.co.sample.emp_management.form.UpdateEmployeeForm;
import jp.co.sample.emp_management.service.EmployeeService;

/**
 * 従業員情報を操作するコントローラー.
 * 
 * @author igamasayuki
 *
 */
@Controller
@RequestMapping("/employee")
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;

	/**
	 * 使用するフォームオブジェクトをリクエストスコープに格納する.
	 * 
	 * @return フォーム
	 */
	@ModelAttribute
	public UpdateEmployeeForm setUpForm() {
		return new UpdateEmployeeForm();
	}

	/////////////////////////////////////////////////////
	// ユースケース：従業員一覧を表示する
	/////////////////////////////////////////////////////
	/**
	 * 従業員一覧画面を出力します.
	 * 
	 * @param model モデル
	 * @return 従業員一覧画面
	 */
	@RequestMapping("/showList")
	public String showList(Model model) {
		List<Employee> employeeList = employeeService.showList();
		model.addAttribute("employeeList", employeeList);
		return "employee/list";
	}

	/////////////////////////////////////////////////////
	// ユースケース：従業員詳細を表示する
	/////////////////////////////////////////////////////
	/**
	 * 従業員詳細画面を出力します.
	 * 
	 * @param id    リクエストパラメータで送られてくる従業員ID
	 * @param model モデル
	 * @return 従業員詳細画面
	 */
	@RequestMapping("/showDetail")
	public String showDetail(String id, Model model) {
		Employee employee = employeeService.showDetail(Integer.parseInt(id));
		model.addAttribute("employee", employee);
		return "employee/detail";
	}

	/////////////////////////////////////////////////////
	// ユースケース：従業員詳細を更新する
	/////////////////////////////////////////////////////
	/**
	 * 従業員詳細(ここでは扶養人数のみ)を更新します.
	 * 
	 * @param form 従業員情報用フォーム
	 * @return 従業員一覧画面へリダクレクト
	 */
	@RequestMapping("/update")
	public String update(@Validated UpdateEmployeeForm form, BindingResult result, Model model) {
		if (result.hasErrors()) {
			return showDetail(form.getId(), model);
		}
		Employee employee = new Employee();
		employee.setId(form.getIntId());
		employee.setDependentsCount(form.getIntDependentsCount());
		employeeService.update(employee);
		return "redirect:/employee/showList";
	}

	/////////////////////////////////////////////////////
	// ユースケース：従業員情報を登録する
	/////////////////////////////////////////////////////
	/**
	 * 使用するフォームオブジェクトをリクエストスコープに格納する.
	 * 
	 * @return フォーム
	 */
	@ModelAttribute
	public InsertEmployeeForm setUpInsertEmployeeForm() {
		return new InsertEmployeeForm();
	}

	/**
	 * 従業員情報登録画面を出力します.
	 * 
	 * @param model モデル
	 * @return 従業員情報登録画面
	 */
	@RequestMapping("/toInsert")
	public String toInsert(Model model) {
		// 性別セレクトボックス
		Map<String, String> genderMap = new LinkedHashMap<>();
		genderMap.put("男性", "男性");
		genderMap.put("女性", "女性");
		model.addAttribute("genderMap", genderMap);

		// 扶養人数セレクトボックス
		Map<Integer, Integer> dependentsCountMap = new LinkedHashMap<>();
		for (int i = 0; i < 10; i++) {
			dependentsCountMap.put(i, i);
		}
		model.addAttribute("dependentsCountMap", dependentsCountMap);

		return "employee/insert";
	}

	/**
	 * 従業員情報を登録します.
	 * 
	 * @param form 従業員情報用フォーム
	 * @return 従業員一覧画面へリダクレクト
	 */
	@RequestMapping("/insert")
	public String insert(@Validated InsertEmployeeForm form, BindingResult result, Model model) throws IOException{
		
		// メールアドレスが重複している場合の処理
		Employee existEmployee = employeeService.findByMailAddress(form.getMailAddress());
		if(existEmployee != null){
			result.rejectValue("mailAddress", "", "そのメールアドレスは既に登録されています");
		}
		
		// 画像ファイル形式チェック
		MultipartFile imageFile = form.getImageFile();
		String fileExtension = null;
		try {
			fileExtension = getExtension(imageFile.getOriginalFilename());
			
			if(!"jpg".equals(fileExtension) && !"png".equals(fileExtension)) {
				result.rejectValue("imageFile", "", "拡張子は.jpgか.pngのみに対応しています");
			}
		} catch (Exception e) {
			result.rejectValue("imageFile", "", "拡張子は.jpgか.pngのみに対応しています");
		}

		
		// 一つでもエラーがあれば入力画面へ戻りエラーメッセージを出す
		if (result.hasErrors()) {
			return toInsert(model);
		}
		

		// 従業員情報を作成
		Employee employee = new Employee();
		BeanUtils.copyProperties(form, employee);
		
		// 画像ファイルをBase64形式にエンコード
		String base64FileString = Base64.getEncoder().encodeToString(imageFile.getBytes());
		if("jpg".equals(fileExtension)) {
			base64FileString = "data:image/jpeg;base64," + base64FileString;
		} else if("png".equals(fileExtension)) {
			base64FileString = "data:image/png;base64," + base64FileString;
		}
		employee.setImage(base64FileString);

		// DBインサート
		employeeService.insert(employee);

		return "redirect:/employee/showList";
	}
	
	/*
	 * ファイル名から拡張子を返します.
	 * @param originalFileName ファイル名
	 * @return .を除いたファイルの拡張子
	 */
	private String getExtension(String originalFileName) throws Exception{
	    if (originalFileName == null) {
	    	throw new FileNotFoundException();
	    }
	    int point = originalFileName.lastIndexOf(".");
	    if (point == -1) {
	    	throw new FileNotFoundException();
	    }
	    return originalFileName.substring(point + 1);
	}
}
