package xjtu.thinkerandperformer.memoryallocator.algorithm;

interface ICommand {
    void invoke(MemManager memManager);

    String getName();
}

class InitCommand implements ICommand {
    int memPoolSize;

    InitCommand(int memPoolSize) {
        this.memPoolSize = memPoolSize;
    }

    @Override
    public void invoke(MemManager memManager) {
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
    public void invoke(MemManager memManager) {
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
    public void invoke(MemManager memManager) {
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
    public void invoke(MemManager memManager) {
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
    public void invoke(MemManager memManager) {
        memManager.write(varName, value);
    }

    @Override
    public String getName() {
        return "write";
    }
}