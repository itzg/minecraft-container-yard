package me.itzg.mccy.services;

import com.flowpowered.nbt.ByteTag;
import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.StringTag;
import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.stream.NBTInputStream;
import me.itzg.mccy.model.LevelDescriptor;
import me.itzg.mccy.model.ServerType;
import me.itzg.mccy.types.ComparableVersion;
import me.itzg.mccy.types.MccyException;
import me.itzg.mccy.types.MccyInvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

import static me.itzg.mccy.types.MccyConstants.SNAPSHOT_VER_PATTERN;

/**
 * Interprets the <code>level.dat</code> file within World save folders
 * @author Geoff Bourne
 * @since 0.2
 */
@Service
public class LevelDatService {
    private static Logger LOG = LoggerFactory.getLogger(LevelDatService.class);

    private static final String TAG_DATA = "Data";

    public LevelDescriptor interpret(InputStream levelDatIn) throws IOException, MccyException {

        final NBTInputStream nbtIn = new NBTInputStream(levelDatIn);

        final Tag rootTag = nbtIn.readTag();
        if (rootTag instanceof CompoundTag) {
            final CompoundTag rootCTag = (CompoundTag) rootTag;
            final Tag<?> tag = rootCTag.getValue().get(TAG_DATA);
            if (tag instanceof CompoundTag) {
                final CompoundTag dataCTag = (CompoundTag) tag;
                LOG.debug("Loaded data tag: {}", dataCTag);

                LevelDescriptor levelDescriptor = new LevelDescriptor();

                final CompoundMap dataMap = dataCTag.getValue();

                levelDescriptor.setName(((StringTag) dataMap.get("LevelName")).getValue());

                extractVersionInfo(dataMap, levelDescriptor);

                resolveServerType(dataMap, levelDescriptor);

                return levelDescriptor;

            }
            else {
                throw new MccyInvalidFormatException("Expected Data tag just below root");
            }

        }
        else {
            throw new MccyInvalidFormatException("Expected root tag to be compound");
        }
    }

    private void resolveServerType(@SuppressWarnings("UnusedParameters") CompoundMap dataMap,
                                   LevelDescriptor levelDescriptor) {
        // for now, just fallback/assume a vanilla server type
        if (levelDescriptor.getServerType() == null) {
            levelDescriptor.setServerType(ServerType.VANILLA);
        }
    }

    protected void extractVersionInfo(CompoundMap dataMap, LevelDescriptor levelDescriptor) {
        if (dataMap.containsKey("BorderSize")) {
            // ge 1.8
            if (dataMap.containsKey("Version")) {
                final CompoundTag versionTag = (CompoundTag) dataMap.get("Version");
                final CompoundMap versionData = versionTag.getValue();

                final ByteTag snapshot = (ByteTag) versionData.get("Snapshot");
                final StringTag versionName = (StringTag) versionData.get("Name");
                if (snapshot.getValue().intValue() == 1) {
                    levelDescriptor.setServerType(ServerType.SNAPSHOT);
                    levelDescriptor.setMinecraftVersion(ComparableVersion.of(versionName.getValue(),
                            SNAPSHOT_VER_PATTERN));
                }
                else {
                    levelDescriptor.setMinecraftVersion(ComparableVersion.of(versionName.getValue()));
                }
            }
            else {
                levelDescriptor.setMinecraftVersion(ComparableVersion.of("1.8"));
            }
        }
        else {
            levelDescriptor.setMinecraftVersion(ComparableVersion.of("1.7"));
        }
    }
}
