import { type IInstrument } from '@/shared/model/instrument.model';

import { type RobotType } from '@/shared/model/enumerations/robot-type.model';
import { type OperationType } from '@/shared/model/enumerations/operation-type.model';
export interface IRobot {
  id?: number;
  type?: keyof typeof RobotType;
  lots?: string;
  period?: number;
  operationType?: keyof typeof OperationType;
  operationCount?: number;
  firstOperationDttm?: Date;
  lastOperationDttm?: Date;
  nextOperationDttm?: Date;
  detectionDttm?: Date;
  lastPrice?: number | null;
  volumeByHour?: number | null;
  instrument?: IInstrument | null;
}

export class Robot implements IRobot {
  constructor(
    public id?: number,
    public type?: keyof typeof RobotType,
    public lots?: string,
    public period?: number,
    public operationType?: keyof typeof OperationType,
    public operationCount?: number,
    public firstOperationDttm?: Date,
    public lastOperationDttm?: Date,
    public nextOperationDttm?: Date,
    public detectionDttm?: Date,
    public lastPrice?: number | null,
    public volumeByHour?: number | null,
    public instrument?: IInstrument | null,
  ) {}
}
