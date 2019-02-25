package jp.co.sample.emp_management.repository;

//Matcher関連メソッドを利用するためのstaticインポート
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import jp.co.sample.emp_management.domain.Administrator;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AdministratorRepositoryTest {

	@Autowired
	private AdministratorRepository administratorRepository;

	@Autowired
	private NamedParameterJdbcTemplate template;

	/**
	 * Administratorオブジェクトを生成するローマッパー.
	 */
	private static final RowMapper<Administrator> ADMINISTRATOR_ROW_MAPPER = (rs, i) -> {
		Administrator administrator = new Administrator();
		administrator.setId(rs.getInt("id"));
		administrator.setName(rs.getString("name"));
		administrator.setMailAddress(rs.getString("mail_address"));
		administrator.setPassword(rs.getString("password"));
		return administrator;
	};

	/**
	 * 
	 */
	@Test
	public void testInsert() {
		Administrator administrator = new Administrator();
		administrator.setName("伊賀将之");
		administrator.setMailAddress("iga@sample.com");
		administrator.setPassword("testtest");
		administratorRepository.insert(administrator);
		System.out.println("インサートが完了しました。");

		Integer maxId = template.queryForObject("select max(id) from administrators;", new MapSqlParameterSource(),
				Integer.class);
		MapSqlParameterSource param = new MapSqlParameterSource().addValue("id", maxId);
		Administrator resultAdministrator = template.queryForObject("select * from administrators where id = :id;",
				param, ADMINISTRATOR_ROW_MAPPER);

		assertThat("名前が登録されていません", resultAdministrator.getName(), is("伊賀将之"));
		assertThat("メールアドレスが登録されていません", resultAdministrator.getMailAddress(), is("iga@sample.com"));
		assertThat("パスワードが登録されていません", resultAdministrator.getPassword(), is("testtest"));
	}

	@Test
	public void testFindByMailAddressAndPassward() {
		Administrator resultAdministrator = administratorRepository.findByMailAddressAndPassward("iga@sample.com",
				"testtest");
		assertThat("名前が検索されていません", resultAdministrator.getName(), is("伊賀将之"));
		assertThat("メールアドレスが検索されていません", resultAdministrator.getMailAddress(), is("iga@sample.com"));
		assertThat("パスワードが検索されていません", resultAdministrator.getPassword(), is("testtest"));
	}

	@Test
	public void tearDownAfterClass() throws Exception {
		MapSqlParameterSource param = new MapSqlParameterSource().addValue("mailAddress", "iga@sample.com");
		template.update("delete from administrators where mail_address = :mailAddress", param);
		System.out.println("入れたデータを削除しました。");
	}
}
