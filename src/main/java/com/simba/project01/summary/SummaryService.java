package com.simba.project01.summary;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import org.springframework.stereotype.Service;

@Service
public class SummaryService
{
    private final Client client = new Client();
    private static final String MODEL = "gemini-2.5-flash";
    private static final int MAX_INPUT_CHARS = 8_000;

    private final GenerateContentConfig config = GenerateContentConfig.builder()
            .temperature(0.2f) //창의성
            .build();

    //한줄 요약
    public String summarize(String text) {
        if(text == null || text.isBlank()) return "리뷰가 하나도 없습니다.";

        // 전처리/길이 제한
        String cleaned = text.replaceAll("\\s{3,}", " ").trim(); //공백이 3개 이상이면 하나로 줄임
        if (cleaned.length() > MAX_INPUT_CHARS) {
            cleaned = cleaned.substring(0, MAX_INPUT_CHARS);
        }

        String prompt = "이 가게의 장단점을 포함하여 한국어로 한 줄로 평가해줘. \n---\n" + cleaned;


        try {
            long t0 = System.nanoTime();
            var res = client.models.generateContent(MODEL, prompt, config);
            long elapsedMs = (System.nanoTime() - t0) / 1_000_000;

            String out = res.text();

            return (out == null || out.isBlank()) ? "요약 결과 없음" : out.trim();

        } catch (Exception e) {
            e.printStackTrace(); // 에러 내용을 콘솔에 출력
            return "요약 중 오류가 발생했습니다.";
        }
    }

}
