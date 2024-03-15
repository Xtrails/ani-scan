import { type IInstrument } from '@/shared/model/instrument.model';

import { type EventType } from '@/shared/model/enumerations/operation-type.model';
import { Robot } from './robot.model';
export interface IRobotEvent {
  type?: keyof typeof EventType;
  data?: [key: Robot];
}

export class RobotEvent implements IRobotEvent {
  constructor(
    public type?: keyof typeof EventType,
    public data?: [key: Robot],
  ) {}
}
