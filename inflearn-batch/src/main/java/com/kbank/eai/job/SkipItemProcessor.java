package com.kbank.eai.job;

import org.springframework.batch.item.ItemProcessor;

public class SkipItemProcessor implements ItemProcessor<String, String> {

    private int cnt = 0;

    @Override
    public String process(String item) throws Exception {

        if(item.equals("6") || item.equals("7")) {
            throw new SkippableException("Process Failed cnt : " + cnt);
        } else {
            System.out.println("ItemProccesor : " + item);
            return String.valueOf(Integer.valueOf(item) * -1);
        }
    }
}
