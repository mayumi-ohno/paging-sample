package jp.co.sample.emp_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.sample.emp_management.domain.Administrator;
import jp.co.sample.emp_management.repository.AdministratorRepository;

/**
 * 管理者情報を操作するサービス.
 * 
 * @author igamasayuki
 *
 */
@Service
@Transactional
public class AdministratorService {
	
	@Autowired
	private AdministratorRepository administratorRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * 管理者情報を登録します.
	 * パスワードはここでハッシュ化されます
	 * 
	 * @param administrator　管理者情報
	 */
	public void insert(Administrator administrator) {
		
		// パスワードをハッシュ化する
		String encodePassword = passwordEncoder.encode(administrator.getPassword());
		administrator.setPassword(encodePassword);
		
		administratorRepository.insert(administrator);
	}
	
//	/**
//	 * ログインをします.(SpringSecurityに任せるためコメントアウトしました)
//	 * @param mailAddress メールアドレス
//	 * @param password パスワード
//	 * @return 管理者情報　存在しない場合はnullが返ります
//	 */
//	public Administrator login(String mailAddress, String passward) {
//		Administrator administrator = administratorRepository.findByMailAddress(mailAddress);
//		// パスワード一致チェック
//		if(passwordEncoder.matches(passward, administrator.getPassword())) {
//			return administrator;
//		}
//		return null;
//	}
	
	/**
	 * メールアドレスから管理者情報を取得します.
	 * 
	 * @param mailAddress メールアドレス
	 * @return 管理者情報 存在しない場合はnullを返します
	 */
	public Administrator findByMailAddress(String mailAddress) {
		return administratorRepository.findByMailAddress(mailAddress);
	}

}
