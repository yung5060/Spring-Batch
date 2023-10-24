package com.kbank.eai.batch.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import com.kbank.eai.domain.ProductVO;

public class ProductRowMapper implements RowMapper<ProductVO> {

	@Override
	public ProductVO mapRow(ResultSet rs, int rowNum) throws SQLException {
		return ProductVO.builder()
				.id(rs.getLong("id"))
				.name(rs.getString("name"))
				.price(rs.getInt("price"))
				.type(rs.getString("type"))
				.build();
	}
	
}
