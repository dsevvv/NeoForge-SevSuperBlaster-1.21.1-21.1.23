package ca.dsevvv.sevsuperblaster.payload;

import ca.dsevvv.sevsuperblaster.SevSuperBlaster;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public record UpdateBlasterBench(BlockPos pos, BlockState state, int value, int flag) implements CustomPacketPayload{
    public static final CustomPacketPayload.Type<UpdateBlasterBench> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(SevSuperBlaster.MODID, "blaster_bench_data"));

    public static final StreamCodec<ByteBuf, UpdateBlasterBench> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodec(BlockPos.CODEC),
            UpdateBlasterBench::pos,
            ByteBufCodecs.fromCodec(BlockState.CODEC),
            UpdateBlasterBench::state,
            ByteBufCodecs.VAR_INT,
            UpdateBlasterBench::value,
            ByteBufCodecs.VAR_INT,
            UpdateBlasterBench::flag,
            UpdateBlasterBench::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
