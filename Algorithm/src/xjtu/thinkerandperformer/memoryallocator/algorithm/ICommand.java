package xjtu.thinkerandperformer.memoryallocator.algorithm;

import xjtu.thinkerandperformer.memoryallocator.algorithm.exception.*;

interface ICommand {
    void invoke(MemManager memManager) throws MemoryManagerException;

    String getName();
}

class InitCommand implements ICommand {
    int memPoolSize;

    InitCommand(int memPoolSize) {
        this.memPoolSize = memPoolSize;
    }

    @Override
    public void invoke(MemManager memManager) throws NumberOutOfBoundsException {
        memManager.init(memPoolSize);
    }

    @Override
    public String getName() {
        return "init";
    }
}

class NewCommand implements ICommand {
    String varName;
    int varSize;

    public NewCommand(String varName, int varSize) {
        this.varName = varName;
        this.varSize = varSize;
    }

    @Override
    public void invoke(MemManager memManager) throws MemoryPoolUninitializedException, InsufficientMemoryPoolException, IllegalParameterException {
        memManager.newVariable(varName, varSize);
    }

    @Override
    public String getName() {
        return "new";
    }
}

class DeleteCommand implements ICommand {
    String varName;

    public DeleteCommand(String varName) {
        this.varName = varName;
    }

    @Override
    public void invoke(MemManager memManager) throws MemoryPoolUninitializedException, VariableNotFoundException {
        memManager.deleteVariable(varName);
    }

    @Override
    public String getName() {
        return "delete";
    }
}

class ReadCommand implements ICommand {
    String varName;

    public ReadCommand(String varName) {
        this.varName = varName;
    }


    @Override
    public void invoke(MemManager memManager) throws VariableNotFoundException, VariableNotAssignedException, MemoryPoolUninitializedException {
        memManager.read(varName);
    }

    @Override
    public String getName() {
        return "read";
    }
}

class WriteCommand implements ICommand {
    String varName;
    String value;

    public WriteCommand(String varName, String value) {
        this.value = value;
        this.varName = varName;
    }


    @Override
    public void invoke(MemManager memManager) throws MemoryPoolUninitializedException, InsufficientVariableSizeException, VariableNotFoundException {
        memManager.write(varName, value);
    }

    @Override
    public String getName() {
        return "write";
    }
}