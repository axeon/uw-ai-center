package uw.ai.center.vendor;

import uw.ai.center.vendor.client.AudioTranscriptionClient;
import uw.ai.center.vo.AiModelConfigData;

/**
 * AUDIO_TRANSCRIPTION 能力接口。
 * <p>声明 vendor 支持实时语音识别能力，{@link #buildAudioTranscriptionClient} 是唯一的构建入口。
 * Vendor 类按需 implements 此接口——类的 implements 列表即能力清单，
 * 无需点开源码查看哪些方法被覆写。
 */
public interface AiAudioTranscriptionVendor extends AiVendor {

    /**
     * 构建 AUDIO_TRANSCRIPTION 客户端实例。
     *
     * @param configData 聚合了 API 配置与模型配置的数据对象
     * @return 具体的 {@link AudioTranscriptionClient} 实例
     */
    AudioTranscriptionClient buildAudioTranscriptionClient(AiModelConfigData configData);
}
