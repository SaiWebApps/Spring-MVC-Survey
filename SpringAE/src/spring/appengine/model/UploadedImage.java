package spring.appengine.model;

public class UploadedImage {

	private long id;
	private long surveyId;
	private long questionId;
	private String fileName;
	private String contentType;
	private byte[] imgBytes;
	
	public long getId() { return id; }
	public long getSurveyId() { return surveyId; }
	public long getQuestionId() { return questionId; }
	public byte[] getImgBytes() { return imgBytes; }
	public String getFileName() { return fileName; }
	public String getContentType() { return contentType; }

	public void setId(long id) { this.id = id; }
	public void setSurveyId(long surveyId) { this.surveyId = surveyId; }
	public void setFileName(String name) { this.fileName = name; }
	public void setQuestionId(long qid) { this.questionId = qid; }
	public void setContentType(String type) { this.contentType = type; }
	public void setImgBytes(byte[] bytes) { this.imgBytes = bytes; }
}