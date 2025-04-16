package uw.ai.center.vendor.ollama;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import uw.common.app.vo.JsonConfigParam;
import uw.common.util.EnumUtils;

/**
 * OllamaParam定义。
 */
public class OllamaParam {

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @Schema(title = "vendor参数", description = "vendor参数")
    enum Vendor implements JsonConfigParam {
        // 定义所有枚举项
        NUMA(ParamType.BOOLEAN, "false", "numa", "是否使用 NUMA。", null),
        NUM_CTX(ParamType.INT, "2048", "num.ctx", "设置用于生成下一个标记的上下文窗口的大小。", null),
        NUM_BATCH(ParamType.INT, "512", "num.batch", "提示处理最大批次大小。", null),
        NUM_GPU(ParamType.INT, "-1", "num.gpu", "发送到 GPU 的数量。在 macOS 上，默认值为 1 以启用 metal 支持，0 以禁用。这里的 1 表示 NumGPU 应动态设置", null),
        MAIN_GPU(ParamType.INT, "0", "main.gpu", "当使用多个 GPU 时，此选项控制使用哪个 GPU 来处理小张量，因为将计算拆分到所有 GPU 上的开销不值得。所指的 GPU 将使用略多的 VRAM 来存储临时结果的临时缓冲区。", null),
        LOW_VRAM(ParamType.BOOLEAN, "false", "low.vram", "启用低 VRAM 模式，优化内存使用。", null),
        F16_KV(ParamType.BOOLEAN, "true", "f16.kv", "使用 16 位浮点数存储键值对以节省内存。", null),
        LOGITS_ALL(ParamType.BOOLEAN, "true", "logits.all", "返回所有标记的 logits，而不仅仅是最后一个。要启用完成返回 logprobs，此选项必须为真。", null),
        VOCAB_ONLY(ParamType.BOOLEAN, "true", "vocab.only", "只加载词汇表，不加载权重（适用于仅需要词汇表的场景）。", null),
        USE_MMAP(ParamType.BOOLEAN, "true", "use.mmap", "默认情况下，模型会被映射到内存中，这使得系统可以根据需要只加载模型的必要部分。然而，如果模型的大小超过了你的总 RAM 量，或者你的系统可用内存不足，使用 mmap 可能会增加页面换出的风险，从而负面影响性能。禁用 mmap 会导致加载时间变慢，但如果不用 mlock，可能会减少页面换出。请注意，如果模型的大小超过了总 RAM 量，关闭 mmap 将阻止模型加载。", null),
        USE_MLOCK(ParamType.BOOLEAN, "false", "use.mlock", "将模型锁定在内存中，防止在内存映射时将其交换出去。这可以提高性能，但会牺牲一些内存映射的优势，因为它需要更多的 RAM 来运行，并且可能在模型加载到 RAM 时减慢加载时间。", null),
        NUM_THREAD(ParamType.INT, "0", "num.thread", "设置在计算过程中使用的线程数量。默认情况下，Ollama 会检测此值以获得最佳性能。建议将此值设置为系统中的物理 CPU 核心数（而不是逻辑核心数）。0 表示由运行时决定。", null),
        NUM_KEEP(ParamType.INT, "4", "num.keep", "保留的模型副本数量。", null),
        SEED(ParamType.INT, "-1", "seed", "设置生成时使用的随机数种子。将此值设置为特定数字，可以在相同的提示下生成相同的文本。", null),
        NUM_PREDICT(ParamType.INT, "-1", "num.predict", "生成文本时预测的最大 token 数。（-1 表示无限生成，-2 表示填充上下文）", null),
        TOP_K(ParamType.INT, "40", "top.k", "减少生成无意义内容的概率。更高的值（例如 100）将给出更多样化的答案，而较低的值（例如 10）将更加保守。", null),
        TOP_P(ParamType.FLOAT, "0.9", "top.p", "与 top-k 一起工作。更高的值（例如，0.95）会导致更多样化的文本，而较低的值（例如，0.5）将生成更集中和保守的文本。", null),
        TFS_Z(ParamType.FLOAT, "1", "tfs.z", "尾部无信息采样用于减少不太可能的标记对输出的影响。更高的值（例如，2.0）将减少这种影响，而值为 1.0 将禁用此设置。", null),
        TYPICAL_P(ParamType.FLOAT, "1", "typical.p", "典型采样策略的参数，用于控制生成文本的多样性。", null),
        REPEAT_LAST_N(ParamType.INT, "64", "repeat.last.n", "设置模型回溯的范围以防止重复。默认值：64，0 表示禁用，-1 表示上下文长度。", null),
        TEMPERATURE(ParamType.FLOAT, "0.8", "temperature", "模型的温度。增加温度会使模型的回答更具创造性（值越大，结果越随机）。", null),
        REPEAT_PENALTY(ParamType.FLOAT, "1.1", "repeat.penalty", "设置重复的惩罚强度。较高的值（例如，1.5）会更强烈地惩罚重复，而较低的值（例如，0.9）会更宽容。", null),
        PRESENCE_PENALTY(ParamType.FLOAT, "0", "presence.penalty", "惩罚在生成文本中出现过的 token，防止重复。", null),
        FREQUENCY_PENALTY(ParamType.FLOAT, "0", "frequency.penalty", "惩罚在生成文本中频繁出现的 token，避免冗余。", null),
        MIROSTAT(ParamType.INT, "0", "mirostat", "启用 mirostat 采样以控制困惑度。（默认: 0, 0 = 禁用, 1 = mirostat, 2 = mirostat 2.0）", null),
        MIROSTAT_TAU(ParamType.INT, "5", "mirostat.tau", "控制输出的连贯性和多样性之间的平衡。较低的值会导致更专注和连贯的文本。", null),
        MIROSTAT_ETA(ParamType.FLOAT, "0.1", "mirostat.eta", "影响算法对生成文本反馈的响应速度。较低的学习率会导致较慢的调整，而较高的学习率会使算法更具响应性。", null),
        PENALIZE_NEWLINE(ParamType.BOOLEAN, "true", "penalize.newline", "启用换行符惩罚，减少重复换行。", null),
        STOP(ParamType.STRING, "-", "stop", "设置要使用的停止序列。当遇到此模式时，LLM 将停止生成文本并返回。可以通过在模型文件中指定多个单独的停止参数来设置多个停止模式。", null),
        FUNCTIONS(ParamType.STRING, "-", "functions", "在单个提示请求中启用的功能列表，通过它们的名称进行标识。这些名称中的功能必须存在于 functionCallbacks 注册表中。", null),
        PROXY_TOOL_CALLS(ParamType.BOOLEAN, "false", "proxy.tool.calls", "如果为真，则将不会处理函数调用，而是将它们代理给客户端。然后是客户端的责任来处理函数调用，将它们分发到适当的函数，并返回结果。如果为假（默认值），则 Spring AI 将内部处理函数调用。仅适用于具有函数调用支持的聊天模型。", null);
        ;

        private final JsonConfigParam.ParamData paramData;

        Vendor(ParamType type, String value, String name, String desc, String regex) {
            this.paramData = new ParamData( EnumUtils.enumNameToDotCase( name() ), type, value, name, desc, regex );
        }

        /**
         * 配置参数数据。
         *
         * @return
         */
        @Override
        public JsonConfigParam.ParamData getParamData() {
            return paramData;
        }

    }


}
