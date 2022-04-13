package me.d3s34.metasploit.msgpack

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer


open class MessagePackSerializer(
    private val nullableMessagePackSerializer: NullableMessagePackSerializer = NullableMessagePackSerializer()
    ): KSerializer<Any> {

    companion object Default: MessagePackSerializer()

    @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
    override val descriptor: SerialDescriptor
        get() = buildSerialDescriptor("MessagePack", SerialKind.CONTEXTUAL)

    override fun deserialize(decoder: Decoder): Any {
        return nullableMessagePackSerializer.deserialize(decoder)!!
    }

    override fun serialize(encoder: Encoder, value: Any) {
        nullableMessagePackSerializer.serialize(encoder, value)
    }
}

open class NullableMessagePackSerializer() : KSerializer<Any?>{

    companion object Default: NullableMessagePackSerializer()

    @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
    override val descriptor: SerialDescriptor
        get() = buildSerialDescriptor("NullableMessagePack", SerialKind.CONTEXTUAL)

    @OptIn(ExperimentalSerializationApi::class)
    override fun deserialize(decoder: Decoder): Any? {
        require(decoder is MessagePackDecoder)

        val typeByte = decoder.peekTypeByte()
        
        return when {
            MessagePackType.Boolean.isBoolean(typeByte) ||
            MessagePackType.Int.isByte(typeByte) ||
            MessagePackType.Int.isShort(typeByte) ||
            MessagePackType.Int.isInt(typeByte) ||
            MessagePackType.Int.isLong(typeByte) ||
            MessagePackType.Float.isFloat(typeByte) ||
            MessagePackType.Float.isDouble(typeByte) ||
            MessagePackType.String.isString(typeByte) -> decoder.decodeValue()
            MessagePackType.Bin.isBinary(typeByte) -> decoder.decodeValue() //TODO: cast to string
            MessagePackType.Array.isArray(typeByte) -> ListSerializer(this).deserialize(decoder)
            MessagePackType.Map.isMap(typeByte) -> MapSerializer(this, this).deserialize(decoder)
            else -> throw MessagePackDeserializeException("Missing decoder for type: $typeByte")
        }
    }

    @OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: Any?) {
        require(encoder is MessagePackEncoder)

        @Suppress("UNCHECKED_CAST")
        when (value) {
            null -> encoder.encodeNull()
            ::isPrimitive -> encoder.encodeValue(value)
            is ByteArray -> encoder.encodeSerializableValue(ByteArraySerializer(), value)
            is List<*> -> ListSerializer(this).serialize(encoder, value.map { it })
            is Array<*> -> ArraySerializer(this).serialize(encoder, value.map { it }.toTypedArray())
            is Map<*, *> -> MapSerializer(this, this).serialize(encoder, value as Map<Any?, Any?>)
            is Map.Entry<*, *> ->
                MapEntrySerializer(this, this).serialize(encoder, value as Map.Entry<Any?, Any?>)
            else -> encoder.encodeSerializableValue(value::class.serializer() as KSerializer<Any>, value)
        }
    }

}