package uw.ai.center.vo;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Rag doc内容包装类。
 */
@Schema(title = "Rag doc内容包装类", description = "Rag doc内容包装类")
public class AiRagDocContent {

    /**
     * 主键ID
     */
    @Schema(title = "主键ID", description = "主键ID")
    private long id;

    /**
     * 内容.
     */
    @Schema(title = "内容", description = "内容")
    private String content;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
