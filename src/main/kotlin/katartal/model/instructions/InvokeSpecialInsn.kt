package katartal.model.instructions

import katartal.model.ByteCode
import katartal.model._Instruction

class InvokeSpecialInsn(cls: String, method: String, description: String) 
    : _Instruction(ByteCode.INVOKESPECIAL, ) 