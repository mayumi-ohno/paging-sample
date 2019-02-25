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
	 * 
	 * @param administrator　管理者情報
	 */
	public void insert(Administrator administrator) {
		administratorRepository.insert(administrator);
	}
	
//	/**
//	 * ログインをします.
//	 * @param mailAddress メールアドレス
//	 * @param password パスワード
//	 * @return 管理者情報　存在しない場合はnullが返ります
//	 */
//	public Administrator login(String mailAddress, String passward) {
//		Administrator administrator = administratorRepository.findByMailAddressAndPassward(mailAddress, passward);
//		return administrator;
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

	/**
	 * パスワードを暗号化する.
	 * 
	 * @param rawPassword
	 *            暗号化前のパスワード(元のパスワード)
	 * @return 暗号化後のパスワード
	 */
	public String encodePassword(String rawPassword) {
		String encodedPassword = passwordEncoder.encode(rawPassword);
		return encodedPassword;
	}
}
