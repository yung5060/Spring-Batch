package com.kbank.eai.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.lang.Nullable;

public class CustomItemStreamReader implements ItemStreamReader<String> {
	
	private final List<String> items;
	private int index = -1;
	private boolean restart = false;
	
	public CustomItemStreamReader(List<String> items) {
		this.items = new ArrayList<>(items);
		this.index = 0;
	}
	

	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		System.out.println("reader stream opened");
		if(executionContext.containsKey("index")) {
			index = executionContext.getInt("index");
			this.restart = true;
		} else {
			index = 0;
			executionContext.put("index", index);
		}
	}

	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {
		executionContext.put("index", index);
	}

	@Override
	public void close() throws ItemStreamException {
		System.out.println("reader stream closed");
	}

	@Override
	@Nullable
	public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		
		String item = null;
		
		if(this.index < this.items.size()) {
			item = this.items.get(index);
			index++;
		}
		
		if(this.index == 6 && !restart) {
			throw new RuntimeException("Restart is required");
		}
		
		return item;
	}

}
