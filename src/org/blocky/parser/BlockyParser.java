package org.blocky.parser;

import org.blocky.engine.BlockyEngine;
import org.blocky.exception.CompilerException;

public interface BlockyParser {

    BlockyEngine parse() throws CompilerException;

}
