package de.mineformers.investiture.serialisation;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTBase;

import java.util.Optional;

/**
 * A translator reads and writes a type to a byte buffer.
 */
public interface Translator<T, N extends NBTBase>
{
    /**
     * Serialise a given object and write it to a buffer.
     * The object's type must be supported by the translator.
     *
     * @param value  the object to serialise
     * @param buffer the buffer the data is to be written to
     */
    @SuppressWarnings("unchecked")
    default void serialise(Object value, ByteBuf buffer)
    {
        // Sort of unnecessary for primitives, but unfortunately the only way of doing this without a lot of special casing.
        buffer.writeBoolean(value != null);
        if (value != null) serialiseImpl((T) value, buffer);
    }

    /**
     * Deserialises the data from a byte buffer and turn it into an object.
     *
     * @param buffer the buffer to read the data from
     * @return an object with the buffer's data
     */
    default T deserialise(ByteBuf buffer)
    {
        // See the serialise method
        if (!buffer.readBoolean())
        {
            return null;
        }
        else
        {
            return deserialiseImpl(buffer);
        }
    }

    /**
     * Serialise a given object and turn it into an NBT value.
     *
     * @param value the object to serialise
     * @return an empty optional if the value was null, a present optional with the serialised NBT value otherwise
     */
    @SuppressWarnings("unchecked")
    default Optional<N> serialise(Object value)
    {
        if (value == null)
            return Optional.empty();
        return Optional.of(serialiseImpl((T) value));
    }

    /**
     * Deserialises the data from an NBT value and turn it into an object.
     *
     * @param tag the optional NBT tag to read the data from, empty if the serialised value was null
     * @return an object with the tag's data
     */
    @SuppressWarnings("unchecked")
    default T deserialise(Optional<NBTBase> tag)
    {
        // See the serialise method
        if (!tag.isPresent())
            return null;
        else
            return deserialiseImpl((N) tag.get());
    }

    /**
     * Implementation of the network serialisation process.
     * WARNING: This should not be called by anyone as it is an internal implementation detail.
     * Calling this method with a null value will most likely result in a crash.
     *
     * @param value  the object to serialise
     * @param buffer the buffer the data is to be written to
     */
    void serialiseImpl(T value, ByteBuf buffer);

    /**
     * Implementation of the network deserialisation process.
     * WARNING: This should not be called by anyone as it is an internal implementation detail.
     * Calling this method directly will most likely result in a crash as the 'null' switch is ignored.
     *
     * @param buffer the buffer to read the data from
     * @return an object with the buffer's data
     */
    T deserialiseImpl(ByteBuf buffer);

    /**
     * Implementation of the NBT serialisation process.
     * WARNING: This should not be called by anyone as it is an internal implementation detail.
     * Calling this method with a null value will most likely result in a crash.
     *
     * @param value the object to serialise
     * @return the NBT representation of the value
     */
    N serialiseImpl(T value);

    /**
     * Implementation of the NBT deserialisation process.
     * WARNING: This should not be called by anyone as it is an internal implementation detail.
     * Calling this method directly will most likely result in a crash as the 'null' switch is ignored.
     *
     * @param tag the NBT tag to read the data from
     * @return an object with the tag's data
     */
    T deserialiseImpl(N tag);
}
