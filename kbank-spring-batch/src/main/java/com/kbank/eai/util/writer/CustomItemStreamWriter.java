package com.kbank.eai.util.writer;

import java.util.List;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;

public class CustomItemStreamWriter implements ItemStreamWriter<String> {

	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		System.out.println("writer stream opened");
	}

	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {
		System.out.println("writer stream updated");
	}

	@Override
	public void close() throws ItemStreamException {
		System.out.println("writer stream closed");
	}

	@Override
	public void write(List<? extends String> items) throws Exception {
		items.forEach(item -> System.out.println(item));
	}

}
