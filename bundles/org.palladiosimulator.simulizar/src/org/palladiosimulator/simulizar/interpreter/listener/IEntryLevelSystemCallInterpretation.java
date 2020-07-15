package org.palladiosimulator.simulizar.interpreter.listener;

import org.palladiosimulator.pcm.usagemodel.EntryLevelSystemCall;

public interface IEntryLevelSystemCallInterpretation {
  public default void beginEntryLevelSystemCallInterpretation(ModelElementPassedEvent<EntryLevelSystemCall> event) {
    
  }

  public default void endEntryLevelSystemCallInterpretation(ModelElementPassedEvent<EntryLevelSystemCall> event) {

  }
}