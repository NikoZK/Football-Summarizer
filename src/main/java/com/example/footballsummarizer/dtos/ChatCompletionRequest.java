package com.example.footballsummarizer.dtos;

import java.util.ArrayList;
import java.util.List;

/*
 This DTO was made by ChatGPT 4.0 using this prompt
 https://chat.openai.com/share/457d16e0-5823-47ab-974b-373a423f9068
 */
public class ChatCompletionRequest {

  private String model;
  private List<Message> messages = new ArrayList<>();
  private double temperature;
  private int max_tokens;
  private double top_p;
  private double frequency_penalty;
  private double presence_penalty;

  public String getModel() { return model; }
  public void setModel(String model) { this.model = model; }
  public List<Message> getMessages() { return messages; }
  public void setMessages(List<Message> messages) { this.messages = messages; }
  public double getTemperature() { return temperature; }
  public void setTemperature(double temperature) { this.temperature = temperature; }
  public int getMax_tokens() { return max_tokens; }
  public void setMax_tokens(int max_tokens) { this.max_tokens = max_tokens; }
  public double getTop_p() { return top_p; }
  public void setTop_p(double top_p) { this.top_p = top_p; }
  public double getFrequency_penalty() { return frequency_penalty; }
  public void setFrequency_penalty(double frequency_penalty) { this.frequency_penalty = frequency_penalty; }
  public double getPresence_penalty() { return presence_penalty; }
  public void setPresence_penalty(double presence_penalty) { this.presence_penalty = presence_penalty; }

  public static class Message {
    private String role;
    private String content;

    public Message(String role, String content) {
      this.role = role;
      this.content = content;
    }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
  }
}
